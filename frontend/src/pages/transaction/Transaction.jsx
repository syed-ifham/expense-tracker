import { useState, useEffect } from 'react';
import { fetchTransactions } from "./transaction.service.js";
import LoadTransaction from "./LoadTransaction.jsx";

export function Transaction() {

  const [pageResponse, setPageResponse] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        setError(null);

        const result = await fetchTransactions(currentPage, 10);
        setPageResponse(result);

        console.log("see : ",result);

      } catch (err) {
        console.error("Caught network fetch exception:", err);
        setError(err.message || "Failed to sync  recent transaction.");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [currentPage]);

  if (loading) {
    return <div className="p-8 text-slate-400 text-sm font-medium">Loading ledger data streams...</div>;
  }

  if (error) {
    return <div className="p-8 text-rose-500 text-sm font-medium">System Alert: {error}</div>;
  }

  if (!pageResponse) {
    return <div className="p-8 text-amber-500 text-sm font-medium">Data channel resolved but payload structure is empty.</div>;
  }

  return (
    <div className="w-full">
      <LoadTransaction
        pageResponse={pageResponse}
        onPageChange={(nextPage) => setCurrentPage(nextPage)}
      />
    </div>
  );
}