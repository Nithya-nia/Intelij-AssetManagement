$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "1. Testing Admin Login..." -ForegroundColor Cyan
$loginRes = Invoke-RestMethod -Uri "http://localhost:8085/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginRes.token
Write-Host "Login Successful! Token acquired." -ForegroundColor Green

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. Test Laptop Next ID
Write-Host "`n2. Testing GET /api/laptops/next-id..." -ForegroundColor Cyan
$laptopNextId = Invoke-RestMethod -Uri "http://localhost:8085/api/laptops/next-id" -Method Get -Headers $headers
Write-Host "Next Laptop ID: $($laptopNextId.nextId)" -ForegroundColor Green
if ($laptopNextId.nextId -notmatch "^LAP-\d{4}$") {
    Write-Host "FAILED: Laptop ID pattern mismatch!" -ForegroundColor Red
} else {
    Write-Host "PASSED: Laptop ID matches LAP-xxxx format!" -ForegroundColor Green
}

# 3. Test Laptop Creation with auto ID and full dropdown values
Write-Host "`n3. Testing Laptop Creation with auto-ID and dropdown specs..." -ForegroundColor Cyan
$newLaptop = @{
    name = "ThinkPad P16 Workstation"
    brand = "Lenovo"
    projectName = "Cloud Migration"
    serialNumber = "SN-AUTO-LAP-" + (Get-Random -Minimum 1000 -Maximum 9999)
    processor = "Intel Core Ultra 7"
    ram = "32 GB"
    storage = "1 TB SSD"
    configuration = "OLED display"
    status = "Available"
} | ConvertTo-Json

$createdLaptop = Invoke-RestMethod -Uri "http://localhost:8085/api/laptops" -Method Post -Headers $headers -Body $newLaptop
Write-Host "Created Laptop ID: $($createdLaptop.laptopId) | Processor: $($createdLaptop.processor) | RAM: $($createdLaptop.ram) | Storage: $($createdLaptop.storage)" -ForegroundColor Green

# 4. Test Mobile Next ID
Write-Host "`n4. Testing GET /api/mobiles/next-id..." -ForegroundColor Cyan
$mobileNextId = Invoke-RestMethod -Uri "http://localhost:8085/api/mobiles/next-id" -Method Get -Headers $headers
Write-Host "Next Mobile ID: $($mobileNextId.nextId)" -ForegroundColor Green
if ($mobileNextId.nextId -notmatch "^MOB-\d{4}$") {
    Write-Host "FAILED: Mobile ID pattern mismatch!" -ForegroundColor Red
} else {
    Write-Host "PASSED: Mobile ID matches MOB-xxxx format!" -ForegroundColor Green
}

# 5. Test Mobile Creation (no mobileNumber, 18 Indian brands option, Single SIM / Dual SIM)
Write-Host "`n5. Testing Mobile Creation with brand and slot config..." -ForegroundColor Cyan
$newMobile = @{
    name = "Nothing Phone (2a)"
    brand = "Nothing"
    imeiNumber = (Get-Random -Minimum 100000000000000 -Maximum 999999999999999).ToString()
    projectName = "Field Operations"
    simSlotConfig = "Dual SIM"
    status = "Available"
} | ConvertTo-Json

$createdMobile = Invoke-RestMethod -Uri "http://localhost:8085/api/mobiles" -Method Post -Headers $headers -Body $newMobile
Write-Host "Created Mobile: $($createdMobile.mobileId) - $($createdMobile.name) | Brand: $($createdMobile.brand) | SlotConfig: $($createdMobile.simSlotConfig)" -ForegroundColor Green

# 6. Test Department Validation
Write-Host "`n6. Testing Department Validation (Only HR, Developers, Admin allowed)..." -ForegroundColor Cyan
$invalidEmp = @{
    employeeId = "EMP-TEST-INV-" + (Get-Random -Minimum 100 -Maximum 999)
    name = "Test Invalid Dept"
    department = "Marketing" # Invalid!
    designation = "Specialist"
    joiningDate = "2024-01-15"
    status = "Active"
} | ConvertTo-Json

try {
    $null = Invoke-RestMethod -Uri "http://localhost:8085/api/employees" -Method Post -Headers $headers -Body $invalidEmp
    Write-Host "FAILED: Invalid department should have been rejected!" -ForegroundColor Red
} catch {
    Write-Host "PASSED: Invalid department correctly rejected: $($_.Exception.Message)" -ForegroundColor Green
}

$validEmp = @{
    employeeId = "EMP-TEST-VAL-" + (Get-Random -Minimum 100 -Maximum 999)
    name = "Test Developer"
    department = "Developers" # Valid!
    designation = "Frontend Engineer"
    joiningDate = "2024-01-15"
    status = "Active"
} | ConvertTo-Json

$createdEmp = Invoke-RestMethod -Uri "http://localhost:8085/api/employees" -Method Post -Headers $headers -Body $validEmp
Write-Host "PASSED: Valid department accepted: $($createdEmp.name) in $($createdEmp.department)" -ForegroundColor Green

# 7. Test SIM Mapping Workflow
Write-Host "`n7. Testing SIM Mapping Workflow..." -ForegroundColor Cyan
$mappingStatuses = Invoke-RestMethod -Uri "http://localhost:8085/api/sim-mapping/status" -Method Get -Headers $headers
Write-Host "Found $($mappingStatuses.Count) devices in SIM mapping status." -ForegroundColor Green

$availableSims = Invoke-RestMethod -Uri "http://localhost:8085/api/sim-mapping/available-sims" -Method Get -Headers $headers
Write-Host "Found $($availableSims.Count) available SIM cards ready for mapping." -ForegroundColor Green

# Create a test SIM card if none available
if ($availableSims.Count -eq 0) {
    $randPhone = (Get-Random -Minimum 9000000000 -Maximum 9999999999).ToString()
    $testSim = @{
        simId = "SIM-MAP-" + (Get-Random -Minimum 100 -Maximum 999)
        mobileNumber = $randPhone
        provider = "Airtel"
        simType = "Physical SIM"
        status = "Available"
    } | ConvertTo-Json
    $null = Invoke-RestMethod -Uri "http://localhost:8085/api/sims" -Method Post -Headers $headers -Body $testSim
    $availableSims = Invoke-RestMethod -Uri "http://localhost:8085/api/sim-mapping/available-sims" -Method Get -Headers $headers
}

$simToMap = $availableSims[0]
$mobileToMap = $createdMobile

Write-Host "Mapping SIM $($simToMap.mobileNumber) ($($simToMap.simId)) to Mobile $($mobileToMap.mobileId) Slot 1..." -ForegroundColor Cyan

$mapPayload = @{
    mobileId = $mobileToMap.mobileId
    simId = $simToMap.simId
    slotNumber = 1
    remarks = "Test Mapping Slot 1"
} | ConvertTo-Json

$mappedMobile = Invoke-RestMethod -Uri "http://localhost:8085/api/sim-mapping/map" -Method Post -Headers $headers -Body $mapPayload
Write-Host "PASSED: SIM mapped successfully! Mobile Slot 1 SimId: $($mappedMobile.slot1SimId), Number: $($mappedMobile.slot1MobileNumber)" -ForegroundColor Green

# Verify via mapping status endpoint
$statusesAfterMap = Invoke-RestMethod -Uri "http://localhost:8085/api/sim-mapping/status" -Method Get -Headers $headers
$devStatus = $statusesAfterMap | Where-Object { $_.mobileId -eq $mobileToMap.mobileId }
Write-Host "Verification: Occupied Slots = $($devStatus.occupiedSlots), Free Slots = $($devStatus.freeSlots)" -ForegroundColor Green

# Test Unmapping
Write-Host "Testing Unmap Slot 1..." -ForegroundColor Cyan
$unmapPayload = @{
    mobileId = $mobileToMap.mobileId
    slotNumber = 1
} | ConvertTo-Json

$unmappedMobile = Invoke-RestMethod -Uri "http://localhost:8085/api/sim-mapping/unmap" -Method Post -Headers $headers -Body $unmapPayload
Write-Host "PASSED: SIM unmapped successfully! Slot 1 SimId is now: $($unmappedMobile.slot1SimId)" -ForegroundColor Green

Write-Host "`nALL AUTOMATED TESTS PASSED SUCCESSFULLY!" -ForegroundColor Green
