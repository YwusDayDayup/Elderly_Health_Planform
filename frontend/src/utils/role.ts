export type UserRole = 'ADMIN' | 'ELDER' | 'STAFF' | 'FAMILY'

export const roleLabelMap: Record<string, string> = {
  ADMIN: '管理员',
  ELDER: '老人用户',
  STAFF: '服务人员',
  FAMILY: '家属用户',
}

export function formatRole(role?: string) {
  if (!role) return '未分配角色'
  return roleLabelMap[role] || role
}

export function getRoleHomePath(role?: string) {
  if (role === 'ADMIN') return '/admin/user'
  if (role === 'STAFF') return '/staff/orders'
  if (role === 'FAMILY') return '/family/bindings'
  return '/elder/services'
}
