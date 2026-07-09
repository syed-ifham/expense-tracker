import {TABLE_HEADER} from './transaction.service.js';
import {ArrowDownLeft, ArrowUpRight, ChevronRight, ChevronLeft} from "lucide-react";

export default function LoadTransaction({pageResponse, onPageChange}) {

  const data = pageResponse?.transactions || pageResponse?.data?.transactions || [];
  const metadata = pageResponse?.metadata || pageResponse?.data?.metadata;

  const page = metadata?.page ?? 0;
  const totalElements = metadata?.totalElements ?? 0;
  const totalPages = metadata?.totalPages ?? 1;

  const limit = 10;
  const currentShowing = data.length;


  return (
    <div
      className="bg-white border border-slate-100 p-6 rounded-3xl shadow-sm w-full flex flex-col justify-between min-h-[380px]">

      {/* Header Section */}
      <div className="mb-5 flex justify-between items-center">
        <div>
          <h3 className="text-slate-900 font-semibold text-lg tracking-tight">
            Recent Transactions
          </h3>
          <p className="text-xs text-slate-400 mt-0.5">Overview of your history logs</p>
        </div>
      </div>

      <div className="w-full overflow-x-auto flex-1">

        <table className="w-full text-left text-sm border-collapse table-auto">
          <thead>
          <tr className="border-b border-slate-100 text-slate-400 uppercase text-[11px] tracking-widest">
            {TABLE_HEADER.map((col, idx) => (
              <th key={idx} className="pb-3 font-bold">
                {col}
              </th>
            ))}
          </tr>
          </thead>

          <tbody className="divide-y divide-slate-50">
          {data.map((tx, i) => {
            const id = tx?.message_id || i;
            const isNegative = tx?.expense ?? true;
            const transactionDate = tx?.transaction_date || "No Date";
            const rawAmount = tx?.amount ?? 0;
            const remittanceName = tx?.remittance || "Unknown Transaction";
            const paymentMethod = tx?.payment_method || "N/A";
            const categoryName = tx?.category || "Uncategorized";

            const Icon = isNegative ? ArrowUpRight : ArrowDownLeft;
            const color = isNegative ? 'text-rose-500' : 'text-emerald-500';
            const iconStyles = isNegative
              ? 'bg-rose-50 text-rose-600 border-rose-100/50'
              : 'bg-emerald-50 text-emerald-600 border-emerald-100/50';

            return (
              <tr
                key={id}
                className="hover:bg-slate-50/60 transition-colors duration-150 ease-in-out group"
              >
                {/*  Date */}
                <td className="py-3.5 text-xs font-medium text-slate-400 group-hover:text-slate-600 transition-colors">
                  {transactionDate}
                </td>

                {/*  Amount  */}
                <td className={`py-3.5 font-semibold tracking-tight text-base ${color}`}>
                  {isNegative ? '-' : '+'}₹ {rawAmount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                </td>

                {/*  Name & Icon */}
                <td className="py-3.5 font-semibold text-slate-800">
                  <div className="flex items-center gap-3">
                    <div className={`w-9 h-9 rounded-xl flex items-center justify-center border shadow-sm transition-transform group-hover:scale-105 ${iconStyles}`}>
                      <Icon size={16} strokeWidth={2.5} />
                    </div>
                    <span className="tracking-wide group-hover:text-slate-900 transition-colors">
            {remittanceName}
          </span>
                  </div>
                </td>

                {/* Method */}
                <td className="py-3.5 text-xs font-medium text-slate-500 group-hover:text-slate-700 transition-colors">
                  {paymentMethod}
                </td>

                {/* Category */}
                <td className="py-3.5">
        <span className="inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-medium bg-slate-100/70 text-slate-600 border border-slate-200/20 capitalize tracking-wide">
          {categoryName}
        </span>
                </td>
              </tr>
            );
          })}
          </tbody>
        </table>

      </div>

      {/* Pagination Footer */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-4 border-t border-slate-50 pt-5 mt-4">

        <p className="text-xs font-medium text-slate-400 tracking-wide">
          Showing <span className="text-slate-800 font-semibold">{page*limit +currentShowing}</span> out of{' '}
          <span className="text-slate-800 font-semibold">{totalElements}</span> transactions
        </p>

        <div className="flex items-center gap-1.5">
          <button
            onClick={() => onPageChange(page - 1)}
            disabled={page === 0}
            className="p-1.5 rounded-lg border border-slate-100 text-slate-400 hover:text-slate-600 hover:bg-slate-50 active:bg-slate-100/80 disabled:opacity-30 disabled:hover:bg-transparent disabled:cursor-not-allowed transition-all duration-150"
          >
            <ChevronLeft size={16} strokeWidth={2.5}/>
          </button>

          <span
            className="text-xs font-semibold text-slate-500 bg-slate-50/60 px-2.5 py-1 rounded-lg border border-slate-100/50">
            {page + 1} / {totalPages || 1}
          </span>

          <button
            onClick={() => onPageChange(page + 1)}
            disabled={page >= totalPages - 1}
            className="p-1.5 rounded-lg border border-slate-100 text-slate-400 hover:text-slate-600 hover:bg-slate-50 active:bg-slate-100/80 disabled:opacity-30 disabled:hover:bg-transparent disabled:cursor-not-allowed transition-all duration-150"
          >
            <ChevronRight size={16} strokeWidth={2.5}/>
          </button>
        </div>

      </div>

    </div>
  );
}