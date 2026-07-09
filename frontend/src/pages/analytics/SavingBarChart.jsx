
const goalsData = [
  { id: 1, name: 'CS Test Series', current: 1000, target: 2657, color: 'bg-indigo-600' },
  { id: 2, name: 'Gym Membership', current: 1000, target: 1199, color: 'bg-emerald-500' },
  { id: 3, name: 'Creatine', current: 499, target: 499, color: 'bg-amber-500' },
];

export  function SavingBarChart() {
  return (
    <div className="bg-white border border-slate-100 p-6 rounded-3xl shadow-sm w-full h-full flex flex-col justify-between min-h-87.5">

      {/* Header */}
      <div className="mb-6">
        <h3 className="text-slate-900 font-semibold text-lg tracking-tight">
          Saving Goals
        </h3>
      </div>

      {/* Goal Items Stack */}
      <div className="space-y-6 flex-1 flex flex-col justify-center">
        {goalsData.map((goal) => {

          const percentage = Math.min((goal.current / goal.target) * 100, 100);

          return (
            <div key={goal.id} className="w-full">
              {/* Meta Info Labels */}
              <div className="flex justify-between items-end mb-2">
                <span className="text-sm font-semibold text-slate-700 tracking-wide">
                  {goal.name}
                </span>
                <span className="text-xs font-medium text-slate-500">
                  ₹{goal.current.toLocaleString('en-IN')} <span className="text-slate-300">/</span> ₹{goal.target.toLocaleString('en-IN')}
                </span>
              </div>

              {/* Progress Bar Track */}
              <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden relative">
                <div
                  className={`h-full ${goal.color} rounded-full transition-all duration-700 ease-out`}
                  style={{ width: `${percentage}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>

    </div>
  );
}