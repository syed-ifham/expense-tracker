import {CARDS} from "./dashboard.js";
import Card from "./Card.jsx";
import {CircleChart} from "../analytics/CircularChart.jsx";
import {MonthlyFlowChart} from "../analytics/MonthlyFlowChart.jsx";
import {SavingBarChart} from "../analytics/SavingBarChart.jsx";
import {Transaction} from "../transaction/Transaction.jsx";

export default function Dashboard() {
  return (
    <div className="space-y-6 p-4 md:p-6 max-w-7xl mx-auto">
      {/*Header*/}
      <div>
        <h1 className="text-slate-800 text-2xl md:text-3xl font-bold tracking-tight">
          Good Morrow, Ifham!
        </h1>
        <p className="text-slate-500 font-medium mt-1">
          What did you do now 👀?
        </p>
      </div>
      {/*CARDS*/}

      <div className="flex flex-wrap gap-4 w-full">
        {CARDS.map((card) => (
          <Card
            key={card.id}
            title={card.title}
            amount={card.amount}
            variant={card.variant}
          />
        ))}
      </div>

      {/*flowchart & circularchart*/}
      <div className="flex flex-col lg:flex-row gap-6 w-full items-start">
        {/* Left Column:  2/3 space */}
        <div className="w-full lg:flex-2">
          <MonthlyFlowChart/>
        </div>

        {/* Right Column: 1/3 space */}
        <div className="w-full lg:flex-1">
          <CircleChart totalBudget={8000} spent={5950}/>
        </div>
      </div>

      {/*Recent Transaction*/}
      <div className="flex flex-col lg:flex-row gap-6 w-full items-start">
        <div className="w-full lg:flex-2">
          <Transaction/>
        </div>
        <div className="w-full lg:flex-1">
          <SavingBarChart/>
        </div>
      </div>

    </div>
  );
}