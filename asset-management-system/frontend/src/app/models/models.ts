export interface AdminUser {
  username: string;
  fullName: string;
  email?: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  fullName: string;
  role: string;
}

export interface Employee {
  id?: string;
  employeeId: string;
  name: string;
  department: string;
  designation: string;
  joiningDate: string;
  email?: string;
  phone?: string;
  status: 'Active' | 'Inactive';
  createdAt?: string;
  updatedAt?: string;
}

export interface Laptop {
  id?: string;
  laptopId: string;
  name: string;
  brand: string;
  projectName: string;
  serialNumber: string;
  configuration?: string;
  processor?: string;
  ram?: string;
  storage?: string;
  purchaseDate?: string;
  warrantyExpiryDate?: string;
  status: 'Available' | 'Assigned' | 'Under Repair' | 'Retired';
  assignedEmployeeId?: string;
  assignedEmployeeName?: string;
  assignedDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface LaptopAssignment {
  id?: string;
  laptopId: string;
  laptopCode: string;
  laptopName: string;
  laptopSerial: string;
  employeeId: string;
  employeeName: string;
  department?: string;
  assignedDate: string;
  returnedDate?: string;
  status: 'Active' | 'Returned';
  returnCondition?: string;
  remarks?: string;
  createdAt?: string;
}

export interface Mobile {
  id?: string;
  mobileId: string;
  name: string;
  brand: string;
  model?: string;
  imeiNumber: string;
  projectName: string;
  simSlotConfig: 'Single SIM' | 'Dual SIM';
  purchaseDate?: string;
  warrantyExpiryDate?: string;
  status: 'Available' | 'Assigned' | 'Under Repair' | 'Retired';
  assignedEmployeeId?: string;
  assignedEmployeeName?: string;
  assignedDate?: string;
  activeSimCount: number;

  // Explicit Slot Mapping
  slot1SimId?: string;
  slot1MobileNumber?: string;
  slot1Provider?: string;
  slot2SimId?: string;
  slot2MobileNumber?: string;
  slot2Provider?: string;

  createdAt?: string;
  updatedAt?: string;
}

export interface MobileAssignment {
  id?: string;
  mobileId: string;
  mobileCode: string;
  mobileName: string;
  imeiNumber: string;
  employeeId: string;
  employeeName: string;
  department?: string;
  assignedDate: string;
  returnedDate?: string;
  status: 'Active' | 'Returned';
  returnCondition?: string;
  remarks?: string;
  createdAt?: string;
}

export interface SimCard {
  id?: string;
  simId: string;
  mobileNumber: string;
  provider: 'Airtel' | 'Jio' | 'Vi' | 'BSNL' | 'Other';
  simSerialNumber?: string;
  simType: 'Physical SIM' | 'eSIM';
  status: 'Available' | 'Assigned' | 'Inactive';
  assignedEmployeeId?: string;
  assignedEmployeeName?: string;
  assignedMobileId?: string;
  assignedMobileName?: string;
  mappedMobileId?: string;
  mappedMobileName?: string;
  latestRechargeAmount?: number;
  latestRechargePlan?: string;
  latestRechargeDate?: string;
  latestPlanEndDate?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SimAssignment {
  id?: string;
  simId: string;
  simCode: string;
  mobileNumber: string;
  provider: string;
  employeeId: string;
  employeeName: string;
  mobileId: string;
  mobileName: string;
  slotNumber?: number;
  issuedDate: string;
  returnedDate?: string;
  status: 'Active' | 'Returned';
  remarks?: string;
  createdAt?: string;
}

export interface Recharge {
  id?: string;
  simId: string;
  mobileNumber: string;
  provider: string;
  employeeId?: string;
  employeeName?: string;
  rechargeAmount: number;
  rechargePlan: string;
  rechargeDate: string;
  planEndDate: string;
  remarks?: string;
  createdAt?: string;
}

export interface NotificationItem {
  id?: string;
  simId: string;
  mobileNumber: string;
  provider: string;
  employeeName: string;
  rechargeAmount: number;
  rechargeDate: string;
  planEndDate: string;
  remainingDays: number;
  expiryStatus: 'Normal' | 'Expiring Soon' | 'Expires Today' | 'Expired';
  calculatedAt?: string;
}

export interface AssetHistory {
  id: string;
  employeeName: string;
  employeeId: string;
  assetType: 'Laptop' | 'Mobile' | 'SIM';
  assetName: string;
  assetIdentifier: string;
  provider?: string;
  assignedDate: string;
  returnedDate?: string;
  assignmentStatus: 'Active' | 'Returned';
  returnCondition?: string;
  remarks?: string;
}

export interface EmployeeAssets {
  employeeId: string;
  employeeName: string;
  assignedLaptop?: Laptop;
  assignedMobile?: Mobile;
  assignedSims?: SimCard[];
  history: AssetHistory[];
}

export interface DashboardStats {
  totalEmployees: number;
  activeEmployees: number;
  inactiveEmployees: number;

  totalLaptops: number;
  assignedLaptops: number;
  availableLaptops: number;
  underRepairLaptops: number;
  retiredLaptops: number;

  totalMobiles: number;
  assignedMobiles: number;
  availableMobiles: number;
  underRepairMobiles: number;
  retiredMobiles: number;

  totalSims: number;
  assignedSims: number;
  availableSims: number;
  inactiveSims: number;

  activeSimAssignments: number;
  expiringSoonSims: number;
  expiredSims: number;

  recentEmployees: Employee[];
  recentAssignments: AssetHistory[];
  simExpiryAlerts: NotificationItem[];
}

export interface SimMappingSlot {
  simId?: string;
  mobileNumber?: string;
  provider?: string;
  isOccupied: boolean;
  isSupported?: boolean;
}

export interface SimMappingDevice {
  id: string;
  mobileId: string;
  name: string;
  brand: string;
  imeiNumber: string;
  projectName: string;
  simSlotConfig: string;
  status: string;
  assignedEmployeeId?: string;
  assignedEmployeeName?: string;
  maxSlots: number;
  slot1: SimMappingSlot;
  slot2: SimMappingSlot;
  occupiedSlots: number;
  freeSlots: number;
}

export interface SimMappingRequest {
  mobileId: string;
  simId: string;
  slotNumber: number;
  remarks?: string;
}

export interface SimUnmapRequest {
  mobileId: string;
  slotNumber?: number;
  simId?: string;
  remarks?: string;
}
