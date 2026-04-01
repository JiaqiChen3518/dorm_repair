import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/student',
      name: 'Student',
      component: () => import('../views/Student.vue'),
      meta: {
        requiresAuth: true,
        role: 1
      }
    },
    {
      path: '/admin',
      name: 'Admin',
      component: () => import('../views/Admin.vue'),
      meta: {
        requiresAuth: true,
        role: 2
      }
    }
  ]
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth;
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('userRole');

  if (requiresAuth) {
    if (!token) {
      next('/login');
    } else {
      if (to.meta.role && to.meta.role.toString() !== userRole) {
        next('/login');
      } else {
        next();
      }
    }
  } else {
    next();
  }
});

export default router;