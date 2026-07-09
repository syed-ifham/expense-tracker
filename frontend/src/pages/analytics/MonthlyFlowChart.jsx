import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const weeklyData = [
  { week: 'Week 1', income: 1000, expense: 5500 },
  { week: 'Week 2', income: 1200, expense: 1100 },
  { week: 'Week 3', income: 1800, expense: 6200 },
  { week: 'Week 4', income: 1000, expense: 1200 },
];

export  function MonthlyFlowChart() {
  return (
    <div className="bg-white border border-slate-100 p-6 rounded-3xl shadow-sm w-full h-90 flex flex-col justify-between">
      {/* Chart Header */}
      <div className="flex justify-between items-center mb-4">
        <div>
          <h3 className="text-slate-900 font-semibold text-lg tracking-tight">
            Last 30 Days
          </h3>
          <p className="text-xs text-slate-400 mt-0.5">Look what you have done!!</p>
        </div>

        {/* Vibrant Legends */}
        <div className="flex gap-4 text-xs font-medium">
          <span className="flex items-center gap-1.5 text-slate-600">
            <span className="w-2.5 h-2.5 rounded-full bg-indigo-600"></span>
            Income
          </span>
          <span className="flex items-center gap-1.5 text-slate-600">
            <span className="w-2.5 h-2.5 rounded-full bg-rose-500"></span>
            Expense
          </span>
        </div>
      </div>

      {/* Chart Visualization */}
      <div className="w-full h-[85%]">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={weeklyData} barGap={8} margin={{ top: 10, right: 5, left: -15, bottom: 0 }}>

            <CartesianGrid strokeDasharray="4 4" stroke="#f1f5f9" vertical={false} />

            <XAxis
              dataKey="week"
              stroke="#64748b"
              fontSize={12}
              fontWeight={500}
              tickLine={false}
              axisLine={false}
              dy={10}
            />
            <YAxis
              stroke="#64748b"
              fontSize={12}
              fontWeight={500}
              tickLine={false}
              axisLine={false}
              tickFormatter={(value) => `₹${value / 1000}k`} // Keeps axis clean
            />

            {/* Light-Theme Tooltip */}
            <Tooltip
              cursor={{ fill: '#f8fafc', opacity: 0.6 }}
              contentStyle={{
                backgroundColor: '#ffffff',
                borderColor: '#e2e8f0',
                borderRadius: '16px',
                boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.05)'
              }}
              labelStyle={{ color: '#0f172a', fontWeight: 600, marginBottom: '4px' }}
            />

            {/* Wider, vibrant bars for the 4-week layout */}
            <Bar dataKey="income" fill="#4f46e5" radius={[5, 5, 0, 0]} barSize={20} />
            <Bar dataKey="expense" fill="#f43f5e" radius={[5, 5, 0, 0]} barSize={20} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}