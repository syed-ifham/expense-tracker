export function CircleChart({ totalBudget = 8000, spent = 5950 }) {

  const percentage = Math.min(spent / totalBudget, 1);
  const radius = 75;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - percentage * circumference;

  return (
    <div className="bg-white border border-slate-100 p-6 rounded-3xl shadow-sm w-full flex flex-col items-center justify-between min-h-90">
      {/* Header Label */}
      <div className="w-full text-left">
        <h3 className="text-slate-900 font-semibold text-lg tracking-tight">
          Monthly Budget
        </h3>
        <p className="text-xs text-slate-400 mt-0.5">Don't you dare cross it</p>
      </div>

      {/* SVG Progress Circle */}
      <div className="relative flex items-center justify-center my-4">
        <svg className="w-44 h-44 transform -rotate-90">
          {/* Subtle Background Track */}
          <circle
            cx="88"
            cy="88"
            r={radius}
            className="stroke-slate-100 fill-none"
            strokeWidth="12"
          />
          <circle
            cx="88"
            cy="88"
            r={radius}
            className="stroke-indigo-600 fill-none transition-all duration-500 ease-out"
            strokeWidth="12"
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
          />
        </svg>

        {/* Floating Center Metrics */}
        <div className="absolute text-center">
          <p className="text-slate-400 text-xs font-medium tracking-wide uppercase">
            Spent
          </p>
          <p className="text-slate-900 text-2xl font-bold tracking-tight mt-0.5">
            ₹{spent.toLocaleString('en-IN')}
          </p>
          <p className="text-slate-400 text-[11px] mt-0.5">
            {Math.round(percentage * 100)}% of total
          </p>
        </div>
      </div>

      {/* Bottom Budget Breakdown */}
      <div className="w-full flex justify-around items-center text-xs text-slate-500 border-t border-slate-50 pt-4">
        <div className="flex flex-col gap-0.5">
          <span className="text-slate-400">Total Limit</span>
          <span className="text-slate-700 font-semibold">₹{totalBudget.toLocaleString('en-IN')}</span>
        </div>
        <div className="flex flex-col gap-0.5 text-right">
          <span className="text-slate-400">Remaining</span>
          <span className="text-emerald-600 font-semibold bg-emerald-50 px-2 py-0.5 rounded-lg">
            ₹{(totalBudget - spent).toLocaleString('en-IN')} left
          </span>
        </div>
      </div>
    </div>
  );
}