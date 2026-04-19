import { http, type Result } from './http'

export interface PageResult<T> {
  total: number
  list: T[]
}

export interface MetricVO {
  label: string
  value: string
  trend: string
}

export interface ContentVO {
  id: number
  contentType: string
  title: string
  summary?: string | null
  content: string
  coverUrl?: string | null
  status: number
  authorName?: string | null
  publishedAt?: string | null
  viewCount: number
}

export interface ServiceOrderVO {
  id: number
  orderNo: string
  elderUserId: number
  elderName: string
  familyUserId?: number | null
  familyName?: string | null
  staffUserId?: number | null
  staffName?: string | null
  serviceProjectId: number
  serviceName: string
  categoryName: string
  serviceTime: string
  serviceAddress: string
  contactName: string
  contactPhone: string
  remark?: string | null
  amount: number
  payStatus: string
  orderStatus: string
  paymentTime?: string | null
  acceptTime?: string | null
  startTime?: string | null
  finishTime?: string | null
  rating?: number | null
  ratingContent?: string | null
  createTime: string
}

export interface HealthAlertVO {
  id: number
  userId: number
  username: string
  nickname: string
  recordId?: number | null
  alertType: string
  alertLevel: string
  title: string
  content: string
  status: string
  createTime: string
}

export interface FamilyBindingVO {
  id: number
  elderUserId: number
  elderName: string
  familyUserId: number
  familyName: string
  relationLabel: string
  status: string
  healthAccess: number
  locationAccess: number
  remark?: string | null
  createTime: string
}

export interface DashboardVO {
  role: string
  title: string
  metrics: MetricVO[]
  latestContents: ContentVO[]
  latestOrders: ServiceOrderVO[]
  latestAlerts: HealthAlertVO[]
  latestBindings: FamilyBindingVO[]
}

export interface ServiceCategoryVO {
  id: number
  name: string
  code: string
  description?: string | null
  sortNo: number
  status: number
}

export interface ServiceCategoryReq {
  name: string
  code: string
  description?: string | null
  sortNo?: number
  status?: number
}

export interface ServiceProjectVO {
  id: number
  categoryId: number
  categoryName?: string | null
  name: string
  description?: string | null
  price: number
  unit: string
  status: number
  coverUrl?: string | null
  defaultStaffUserId?: number | null
  defaultStaffName?: string | null
  serviceDurationMinutes: number
}

export interface ServiceProjectReq {
  categoryId: number
  name: string
  description?: string | null
  price: number
  unit?: string
  status?: number
  coverUrl?: string | null
  defaultStaffUserId?: number | null
  serviceDurationMinutes?: number
}

export interface CreateOrderReq {
  elderUserId?: number | null
  serviceProjectId: number
  serviceTime: string
  serviceAddress: string
  contactName: string
  contactPhone: string
  remark?: string | null
}

export interface RateOrderReq {
  rating: number
  ratingContent?: string | null
}

export interface FamilyBindingReq {
  elderUserId?: number | null
  familyUserId?: number | null
  relationLabel: string
  healthAccess?: number
  locationAccess?: number
  remark?: string | null
}

export interface ElderProfileVO {
  id?: number | null
  userId: number
  username: string
  nickname: string
  phone?: string | null
  realName?: string | null
  idCard?: string | null
  emergencyContactName?: string | null
  emergencyContactPhone?: string | null
  address?: string | null
  healthNotes?: string | null
  familyHealthAuthorized: number
  familyLocationAuthorized: number
}

export interface ElderProfileReq {
  realName?: string | null
  idCard?: string | null
  emergencyContactName?: string | null
  emergencyContactPhone?: string | null
  address?: string | null
  healthNotes?: string | null
  familyHealthAuthorized?: number
  familyLocationAuthorized?: number
}

export interface StaffProfileVO {
  id?: number | null
  userId: number
  username: string
  nickname: string
  phone?: string | null
  realName?: string | null
  certificateNo?: string | null
  specialty?: string | null
  serviceRadiusKm: number
  intro?: string | null
  auditStatus: string
}

export interface StaffProfileReq {
  realName?: string | null
  certificateNo?: string | null
  specialty?: string | null
  serviceRadiusKm?: number
  intro?: string | null
}

export interface HealthRecordVO {
  id: number
  userId: number
  username: string
  nickname: string
  recordDate: string
  weight?: number | null
  systolicBp?: number | null
  diastolicBp?: number | null
  bloodSugar?: number | null
  bloodLipid?: number | null
  pulse?: number | null
  bodyTemp?: number | null
  checkInFlag: number
  note?: string | null
  abnormalFlag: number
  createTime: string
}

export interface HealthRecordReq {
  recordDate?: string | null
  weight?: number | null
  systolicBp?: number | null
  diastolicBp?: number | null
  bloodSugar?: number | null
  bloodLipid?: number | null
  pulse?: number | null
  bodyTemp?: number | null
  checkInFlag?: number
  note?: string | null
}

export interface SafeZoneVO {
  id?: number | null
  userId: number
  centerLatitude: number
  centerLongitude: number
  radiusMeters: number
  address?: string | null
}

export interface SafeZoneReq {
  centerLatitude: number
  centerLongitude: number
  radiusMeters: number
  address?: string | null
}

export interface LocationVO {
  id: number
  userId: number
  username: string
  nickname: string
  latitude: number
  longitude: number
  address?: string | null
  withinZone: number
  createTime: string
}

export interface LocationReportReq {
  latitude: number
  longitude: number
  address?: string | null
}

export interface ExternalCallLogVO {
  id: number
  providerType: string
  action: string
  bizType?: string | null
  bizId?: string | null
  reqJson?: string | null
  respJson?: string | null
  success: number
  createTime: string
}

export interface CommunityPostVO {
  id: number
  authorId: number
  authorName: string
  title: string
  content: string
  category: string
  postStatus: string
  auditStatus: string
  commentCount: number
  likeCount: number
  viewCount: number
  lastAction?: string | null
  lastActionTime?: string | null
  createTime: string
}

export interface OpLogVO {
  id: number
  userId?: number | null
  username: string
  action: string
  method: string
  path: string
  success: number
  errorMsg?: string | null
  costMs?: number | null
  createTime: string
}

export interface ContentReq {
  contentType: string
  title: string
  summary?: string | null
  content: string
  coverUrl?: string | null
  status?: number
}

export function getMyDashboard() {
  return http.get<any, Result<DashboardVO>>('/api/me/dashboard')
}

export function getAdminDashboard() {
  return http.get<any, Result<DashboardVO>>('/api/admin/care/dashboard')
}

export function listPublicContents(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ContentVO>>>('/api/public/contents', { params })
}

export function listAdminContents(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ContentVO>>>('/api/admin/care/contents', { params })
}

export function createAdminContent(data: ContentReq) {
  return http.post<any, Result<null>>('/api/admin/care/contents', data)
}

export function updateAdminContent(id: number, data: ContentReq) {
  return http.put<any, Result<null>>(`/api/admin/care/contents/${id}`, data)
}

export function deleteAdminContent(id: number) {
  return http.delete<any, Result<null>>(`/api/admin/care/contents/${id}`)
}

export function listPublicServiceCategories() {
  return http.get<any, Result<ServiceCategoryVO[]>>('/api/public/service-categories')
}

export function listAdminServiceCategories() {
  return http.get<any, Result<ServiceCategoryVO[]>>('/api/admin/care/service-categories')
}

export function createAdminServiceCategory(data: ServiceCategoryReq) {
  return http.post<any, Result<null>>('/api/admin/care/service-categories', data)
}

export function updateAdminServiceCategory(id: number, data: ServiceCategoryReq) {
  return http.put<any, Result<null>>(`/api/admin/care/service-categories/${id}`, data)
}

export function deleteAdminServiceCategory(id: number) {
  return http.delete<any, Result<null>>(`/api/admin/care/service-categories/${id}`)
}

export function listPublicServiceProjects(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ServiceProjectVO>>>('/api/public/service-projects', { params })
}

export function listAdminServiceProjects(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ServiceProjectVO>>>('/api/admin/care/service-projects', { params })
}

export function createAdminServiceProject(data: ServiceProjectReq) {
  return http.post<any, Result<null>>('/api/admin/care/service-projects', data)
}

export function updateAdminServiceProject(id: number, data: ServiceProjectReq) {
  return http.put<any, Result<null>>(`/api/admin/care/service-projects/${id}`, data)
}

export function deleteAdminServiceProject(id: number) {
  return http.delete<any, Result<null>>(`/api/admin/care/service-projects/${id}`)
}

export function myServiceOrders(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ServiceOrderVO>>>('/api/me/service-orders', { params })
}

export function createServiceOrder(data: CreateOrderReq) {
  return http.post<any, Result<ServiceOrderVO>>('/api/me/service-orders', data)
}

export function payServiceOrder(id: number) {
  return http.post<any, Result<ServiceOrderVO>>(`/api/me/service-orders/${id}/pay`)
}

export function cancelServiceOrder(id: number) {
  return http.post<any, Result<ServiceOrderVO>>(`/api/me/service-orders/${id}/cancel`)
}

export function acceptServiceOrder(id: number) {
  return http.post<any, Result<ServiceOrderVO>>(`/api/me/service-orders/${id}/accept`)
}

export function startServiceOrder(id: number) {
  return http.post<any, Result<ServiceOrderVO>>(`/api/me/service-orders/${id}/start`)
}

export function completeServiceOrder(id: number) {
  return http.post<any, Result<ServiceOrderVO>>(`/api/me/service-orders/${id}/complete`)
}

export function rateServiceOrder(id: number, data: RateOrderReq) {
  return http.post<any, Result<ServiceOrderVO>>(`/api/me/service-orders/${id}/rate`, data)
}

export function listAdminServiceOrders(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ServiceOrderVO>>>('/api/admin/care/service-orders', { params })
}

export function assignAdminServiceOrder(id: number, staffUserId: number) {
  return http.post<any, Result<ServiceOrderVO>>(`/api/admin/care/service-orders/${id}/assign`, { staffUserId })
}

export function getMyElderProfile() {
  return http.get<any, Result<ElderProfileVO>>('/api/me/elder-profile')
}

export function updateMyElderProfile(data: ElderProfileReq) {
  return http.put<any, Result<ElderProfileVO>>('/api/me/elder-profile', data)
}

export function getMyStaffProfile() {
  return http.get<any, Result<StaffProfileVO>>('/api/me/staff-profile')
}

export function updateMyStaffProfile(data: StaffProfileReq) {
  return http.put<any, Result<StaffProfileVO>>('/api/me/staff-profile', data)
}

export function listAdminElderProfiles(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ElderProfileVO>>>('/api/admin/care/elder-profiles', { params })
}

export function listAdminStaffProfiles(params: Record<string, any>) {
  return http.get<any, Result<PageResult<StaffProfileVO>>>('/api/admin/care/staff-profiles', { params })
}

export function auditAdminStaffProfile(id: number, auditStatus: string) {
  return http.post<any, Result<null>>(`/api/admin/care/staff-profiles/${id}/audit`, { auditStatus })
}

export function myFamilyBindings(params: Record<string, any>) {
  return http.get<any, Result<PageResult<FamilyBindingVO>>>('/api/me/family-bindings', { params })
}

export function createFamilyBinding(data: FamilyBindingReq) {
  return http.post<any, Result<FamilyBindingVO>>('/api/me/family-bindings', data)
}

export function auditFamilyBinding(id: number, approved: boolean) {
  return http.post<any, Result<FamilyBindingVO>>(`/api/me/family-bindings/${id}/audit`, { approved })
}

export function deleteFamilyBinding(id: number) {
  return http.delete<any, Result<null>>(`/api/me/family-bindings/${id}`)
}

export function listAdminFamilyBindings(params: Record<string, any>) {
  return http.get<any, Result<PageResult<FamilyBindingVO>>>('/api/admin/care/family-bindings', { params })
}

export function myHealthRecords(params: Record<string, any>) {
  return http.get<any, Result<PageResult<HealthRecordVO>>>('/api/me/health-records', { params })
}

export function createHealthRecord(data: HealthRecordReq) {
  return http.post<any, Result<HealthRecordVO>>('/api/me/health-records', data)
}

export function listAdminHealthAlerts(params: Record<string, any>) {
  return http.get<any, Result<PageResult<HealthAlertVO>>>('/api/admin/care/health-alerts', { params })
}

export function getSafeZone(targetUserId?: number) {
  return http.get<any, Result<SafeZoneVO | null>>('/api/me/safe-zone', { params: { targetUserId } })
}

export function updateSafeZone(data: SafeZoneReq) {
  return http.put<any, Result<SafeZoneVO>>('/api/me/safe-zone', data)
}

export function reportLocation(data: LocationReportReq) {
  return http.post<any, Result<LocationVO>>('/api/me/locations', data)
}

export function getLatestLocation(targetUserId?: number) {
  return http.get<any, Result<LocationVO | null>>('/api/me/locations/latest', { params: { targetUserId } })
}

export function listAdminExternalCallLogs(params: Record<string, any>) {
  return http.get<any, Result<PageResult<ExternalCallLogVO>>>('/api/admin/care/external-call-logs', { params })
}

export function listAdminCommunityPosts(params: Record<string, any>) {
  return http.get<any, Result<PageResult<CommunityPostVO>>>('/api/admin/care/community-posts', { params })
}

export function listAdminOpLogs(params: Record<string, any>) {
  return http.get<any, Result<PageResult<OpLogVO>>>('/api/admin/care/op-logs', { params })
}
