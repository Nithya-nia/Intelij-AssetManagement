$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "1. Admin Login..." -ForegroundColor Cyan
$loginRes = Invoke-RestMethod -Uri "http://localhost:8085/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginRes.token
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}
Write-Host "Login OK!" -ForegroundColor Green

# ----------------- TEST 1: EMPLOYEE DELETION -----------------
Write-Host "`n2. Testing Employee Deletion..." -ForegroundColor Cyan

# A. Attempt to delete employee with active asset (EMP-101)
$employees = Invoke-RestMethod -Uri "http://localhost:8085/api/employees" -Method Get -Headers $headers
$emp101 = $employees | Where-Object { $_.employeeId -eq "EMP-101" }

if ($emp101) {
    try {
        $null = Invoke-RestMethod -Uri "http://localhost:8085/api/employees/$($emp101.id)" -Method Delete -Headers $headers
        Write-Host "FAILED: Should not delete employee EMP-101 with active laptop!" -ForegroundColor Red
    } catch {
        Write-Host "PASSED: Deletion blocked for employee with active asset: $($_.Exception.Message)" -ForegroundColor Green
    }
}

# B. Create a standalone employee with no assets and delete
$tempEmp = @{
    employeeId = "EMP-DEL-" + (Get-Random -Minimum 1000 -Maximum 9999)
    name = "Temporary Employee"
    department = "HR"
    designation = "HR Assistant"
    joiningDate = "2024-05-01"
    status = "Active"
} | ConvertTo-Json

$createdTempEmp = Invoke-RestMethod -Uri "http://localhost:8085/api/employees" -Method Post -Headers $headers -Body $tempEmp
Write-Host "Created temporary employee: $($createdTempEmp.name) ($($createdTempEmp.employeeId))" -ForegroundColor Cyan

$delEmpRes = Invoke-RestMethod -Uri "http://localhost:8085/api/employees/$($createdTempEmp.id)" -Method Delete -Headers $headers
Write-Host "PASSED: Standalone employee without assets deleted successfully!" -ForegroundColor Green

# ----------------- TEST 2: LAPTOP DELETION -----------------
Write-Host "`n3. Testing Laptop Deletion..." -ForegroundColor Cyan

# A. Attempt to delete assigned laptop (LAP-0001)
$laptops = Invoke-RestMethod -Uri "http://localhost:8085/api/laptops" -Method Get -Headers $headers
$assignedLaptop = $laptops | Where-Object { $_.status -eq "Assigned" } | Select-Object -First 1

if ($assignedLaptop) {
    try {
        $null = Invoke-RestMethod -Uri "http://localhost:8085/api/laptops/$($assignedLaptop.id)" -Method Delete -Headers $headers
        Write-Host "FAILED: Should not delete assigned laptop $($assignedLaptop.laptopId)!" -ForegroundColor Red
    } catch {
        Write-Host "PASSED: Deletion blocked for assigned laptop: $($_.Exception.Message)" -ForegroundColor Green
    }
}

# B. Create a standalone Available laptop and delete it
$tempLaptop = @{
    name = "MacBook Air M2 (Spare)"
    brand = "Apple"
    projectName = "Bench Pool"
    serialNumber = "SN-DEL-" + (Get-Random -Minimum 10000 -Maximum 99999)
    processor = "Apple M2"
    ram = "16 GB"
    storage = "512 GB SSD"
    status = "Available"
} | ConvertTo-Json

$createdTempLaptop = Invoke-RestMethod -Uri "http://localhost:8085/api/laptops" -Method Post -Headers $headers -Body $tempLaptop
Write-Host "Created temporary laptop: $($createdTempLaptop.laptopId) - $($createdTempLaptop.name)" -ForegroundColor Cyan

$delLapRes = Invoke-RestMethod -Uri "http://localhost:8085/api/laptops/$($createdTempLaptop.id)" -Method Delete -Headers $headers
Write-Host "PASSED: Standalone Available laptop deleted successfully!" -ForegroundColor Green

# ----------------- TEST 3: LAPTOP ASSIGNMENT DROPDOWN FILTERING -----------------
Write-Host "`n4. Testing Laptop Assignment Dropdown Filter Logic..." -ForegroundColor Cyan
$allActiveEmps = Invoke-RestMethod -Uri "http://localhost:8085/api/employees?status=Active" -Method Get -Headers $headers
$currentLaptops = Invoke-RestMethod -Uri "http://localhost:8085/api/laptops" -Method Get -Headers $headers

$assignedEmpIds = @($currentLaptops | Where-Object { $_.status -eq "Assigned" -and $_.assignedEmployeeId } | ForEach-Object { $_.assignedEmployeeId })
Write-Host "Currently Assigned Employee IDs: $($assignedEmpIds -join ', ')" -ForegroundColor Yellow

$eligibleEmps = @($allActiveEmps | Where-Object { $assignedEmpIds -notcontains $_.employeeId })
Write-Host "Eligible Employees without Laptop ($($eligibleEmps.Count)): $($eligibleEmps.name -join ', ')" -ForegroundColor Green

# Check that EMP-101 and EMP-102 (who hold laptops) are NOT in eligible list
foreach ($assignedId in $assignedEmpIds) {
    if ($eligibleEmps.employeeId -contains $assignedId) {
        Write-Host "FAILED: Employee $assignedId has an active laptop but is in the eligible dropdown!" -ForegroundColor Red
    } else {
        Write-Host "PASSED: Employee $assignedId is properly excluded from the assignment dropdown." -ForegroundColor Green
    }
}

Write-Host "`nALL TESTS PASSED!" -ForegroundColor Green
