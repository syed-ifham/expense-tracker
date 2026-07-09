import { Hourglass, ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function ComingSoon() {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center min-h-[80vh] px-4 tracking-tight">

      {/* Premium Abstract Illustration Box */}
      <div className="relative w-48 h-48 mb-8 flex items-center justify-center">
        {/* Decorative ambient background blur */}
        <div className="absolute inset-0 bg-blue-500/10 rounded-full blur-2xl animate-pulse" />

        {/* Concentric Curvy Rings */}
        <div className="absolute w-40 h-40 border-2 border-dashed border-slate-200 rounded-full animate-[spin_60s_linear_infinite]" />
        <div className="absolute w-32 h-32 bg-slate-50 border border-slate-200/60 rounded-[2rem] shadow-sm transform rotate-12" />

        {/* Floating Core Card */}
        <div className="relative w-24 h-24 bg-white border border-slate-200/80 rounded-2xl shadow-md flex flex-col items-center justify-center transition-transform hover:scale-105 duration-300">
          <div className="w-10 h-10 rounded-xl bg-blue-50 flex items-center justify-center text-blue-600 mb-1">
            <Hourglass size={20} className="animate-[spin_4s_ease-in-out_infinite]" />
          </div>
          <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">v2.0</span>
        </div>
      </div>

      {/* Elegant Typography Status */}
      <div className="text-center max-w-sm">
        <span className="text-xs font-bold text-blue-600 tracking-widest uppercase bg-blue-50 px-3 py-1 rounded-full">
          Under Construction
        </span>
        <h2 className="text-2xl font-bold text-slate-800 mt-4 tracking-tight">
          Feature Arriving Soon
        </h2>
        <p className="text-xs font-medium text-slate-400 mt-2 leading-relaxed">
          Ifham putting the finishing touches on this module. Hold tight, it's going to be exceptional.
        </p>
      </div>

      {/* Call to Action Button */}
      <button
        onClick={() => navigate('/')}
        type="button"
        className="flex items-center gap-2 mt-8 px-4 py-2 rounded-xl text-xs font-bold text-slate-600 bg-white border border-slate-200/60 shadow-sm hover:bg-slate-50 hover:text-slate-900 transition-all active:scale-[0.98]"
      >
        <ArrowLeft size={14} />
        Back to Dashboard
      </button>

    </div>
  );
}