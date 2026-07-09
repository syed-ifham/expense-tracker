import { LayoutDashboard, ArrowRightLeft,  Target, PieChart, Activity, Settings } from 'lucide-react';

export const SIDEBAR_LINKS = [
  { id: 1, label: 'Dashboard', icon: LayoutDashboard, path: '/' },
  { id: 2, label: 'Transactions', icon: ArrowRightLeft, path: '/transactions' },
  { id: 3, label: 'Analytics', icon: Activity, path: '/analytics' },
  { id: 4, label: 'Budget', icon: PieChart, path: '/budget' },
  { id: 5, label: 'Goals', icon: Target, path: '/goals' },
  { id: 6, label: 'Settings', icon: Settings, path: '/settings' },
];
