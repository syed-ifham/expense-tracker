import { Hexagon, Sun, Moon } from 'lucide-react';
import { SIDEBAR_LINKS } from "./sidebar.js";
import { NavLink } from "react-router-dom";
import { useState } from "react";

export default function SideBar() {

  const [isDarkMode, setIsDarkMode] = useState(false);

  return (
    <div className="bg-white h-full border border-slate-200/60 flex flex-col justify-between p-5 rounded-[2rem] shadow-sm">

      <div>
        {/* Brand Logo Header */}
        <div className="flex items-center mb-8 gap-3 px-2">
          <Hexagon className="fill-blue-600 text-blue-600" size={28} />
          <h1 className="text-slate-900 text-xl font-bold tracking-tight">Expensify</h1>
        </div>

        {/* Navigation Section */}
        <nav className="flex flex-col gap-1.5">
          {SIDEBAR_LINKS.map((link) => {
            const Icon = link.icon;
            return (
              <NavLink
                key={link.id}
                to={link.path}
                className={({ isActive }) =>
                  `flex items-center gap-3.5 px-4 py-3 w-full text-sm rounded-xl font-medium transition-all duration-200
                     ${isActive
                    ? 'bg-blue-600 text-white shadow-sm shadow-blue-600/10 font-semibold'
                    : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900'
                  }`
                }
              >
                <Icon size={18} />
                {link.label}
              </NavLink>
            );
          })}
        </nav>
      </div>

      {/* Bottom Actions Footer */}
      <nav className="w-full flex flex-col items-center gap-4 border-t border-slate-100 pt-5">

        {/* Theme Controller Toggle */}
        <div className="flex w-full justify-center">
          <button
            onClick={() => setIsDarkMode(!isDarkMode)}
            type="button"
            className="relative flex items-center h-10 w-20 rounded-full p-1 bg-slate-100 cursor-pointer select-none"
          >
            {/* Sliding Active Background Pill */}
            <div
              className={`absolute h-8 w-9 rounded-full bg-white shadow-sm transition-all duration-300 ease-in-out
                ${isDarkMode ? 'translate-x-9' : 'translate-x-0'}`}
            />

            {/* Icons Container */}
            <div className="relative z-10 flex w-full justify-around items-center h-full">
              <Sun
                size={16}
                className={`transition-colors duration-300 ${!isDarkMode ? 'text-slate-900 stroke-[2.5]' : 'text-slate-400'}`}
              />
              <Moon
                size={16}
                className={`transition-colors duration-300 ${isDarkMode ? 'text-slate-900 stroke-[2.5]' : 'text-slate-400'}`}
              />
            </div>
          </button>
        </div>

        {/* Logout System Trigger */}
        <div className="w-full">
          <button
            type="button"
            className="w-full py-2.5 rounded-xl font-medium text-sm text-slate-600 bg-slate-50 hover:bg-red-50 hover:text-red-600 active:scale-[0.98] transition-all border border-slate-100 hover:border-red-100"
          >
            Logout
          </button>
        </div>
      </nav>

    </div>
  );
}