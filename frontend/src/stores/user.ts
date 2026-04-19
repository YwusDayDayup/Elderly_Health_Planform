import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getProfile, getMyRoles } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>({
    username: '',
    nickname: '',
    avatarUrl: '',
    email: '',
    phone: '',
    gender: 0,
    birthday: null,
    bio: '',
    signature: '',
    region: '',
    city: '',
    role: 'ELDER',
    roles: ['ELDER'],
  })

  const setToken = (t: string) => {
    token.value = t
    localStorage.setItem('token', t)
  }

  const setUserInfo = (info: any) => {
    userInfo.value = { ...userInfo.value, ...info }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {
      username: '',
      nickname: '',
      avatarUrl: '',
      email: '',
      phone: '',
      gender: 0,
      birthday: null,
      bio: '',
      signature: '',
      region: '',
      city: '',
      role: 'ELDER',
      roles: ['ELDER'],
    }
    localStorage.removeItem('token')
  }

  const fetchMe = async () => {
    const [profileRes, roleRes] = await Promise.all([getProfile(), getMyRoles()])
    if (profileRes.success) {
      setUserInfo(profileRes.data)
    }
    if (roleRes.success) {
      setUserInfo({ role: roleRes.data.role, roles: [roleRes.data.role] })
    }
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    logout,
    fetchMe,
  }
})
