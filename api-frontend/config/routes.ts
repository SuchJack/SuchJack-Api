
export default [
  { path: '/', name: '欢迎访问', icon: 'smile', component: './Index'},
  { path: '/interface_info', name: '接口列表', icon: 'table', component: './Interface'},
  // { path: '/information', name: '个人中心', icon: 'user', component: './UserInformation'},
  // { path: '/interface_info', name: '版本说明', icon: 'question', component: './Interface',},
  { path: '/interface_info/:id', name: '查看接口', icon: 'smile', component: './InterfaceInfo', hideInMenu:true},
  {
    path: '/user',
    layout: false,
    routes: [{ name: '登录', path: '/user/login', component: './User/Login' },
          // { name: '信息', path: '/user/info', component: './User/UserInformation' }
    ],
  },
  {
    path: '/account',
    name: '个人中心',
    icon: 'user',
    access: 'canUser',
    routes: [{ name: '账户信息', path: '/account/center', component: './Account/Center' },
      // { name: '信息', path: '/user/info', component: './User/UserInformation' }
    ],
  },
  {
    path: '/admin',
    name: '管理员页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      // { path: '/admin', redirect: '/admin/sub-page' },
      // { path: '/admin/sub-page', name: '二级管理页', component: './Admin' },
      {name:'接口管理',icon:'table',path:'/admin/interface_info',component: './Admin/InterfaceInfo'},
      {name:'接口分析',icon:'analysis',path:'/admin/interface_analysis',component: './Admin/InterfaceAnalysis'},
      { name: '用户管理',icon:'analysis', path: '/admin/user_info', component: './Admin/UserInfo' },
    ],
  },
  // { name: '查询表格', icon: 'table', path: '/list', component: './InterfaceInfo' },
  // { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
// @ts-ignore
