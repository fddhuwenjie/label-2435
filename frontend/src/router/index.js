import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/rooms',
    children: [
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('../views/user/Rooms.vue'),
        meta: { title: '房间查询' }
      },
      {
        path: 'reservations',
        name: 'MyReservations',
        component: () => import('../views/user/MyReservations.vue'),
        meta: { title: '我的预订' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/user/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/admin/users',
    meta: { requiresAdmin: true },
    children: [
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'rooms',
        name: 'AdminRooms',
        component: () => import('../views/admin/Rooms.vue'),
        meta: { title: '房间管理' }
      },
      {
        path: 'reservations',
        name: 'AdminReservations',
        component: () => import('../views/admin/Reservations.vue'),
        meta: { title: '预订管理' }
      },
      {
        path: 'deposits',
        name: 'AdminDeposits',
        component: () => import('../views/admin/Deposits.vue'),
        meta: { title: '定金管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 不需要登录的页面
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  // 需要登录
  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/rooms')
    return
  }

  next()
})

export default router
