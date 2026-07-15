import {MonthlyFlowChart} from "./MonthlyFlowChart.jsx";
import {CircleChart} from "./CircularChart.jsx";
import {SavingBarChart} from "./SavingBarChart.jsx";

export const Analytics = () => {
  return (
    <>
      <div>
        <div className="flex flex-col gap-6 p-4 sm:p-6 lg:p-8 max-w-[1600px] mx-auto w-full transition-all duration-300">

          <div className="flex flex-col xl:flex-row gap-6 w-full items-stretch">
            <div className="w-full xl:flex-[2] bg-white  p-5 rounded-3xl border border-zinc-100  shadow-xs">
              <MonthlyFlowChart />
            </div>

            <div className="w-full xl:flex-[1] bg-white  p-5 rounded-3xl border border-zinc-100  shadow-xs">
              <CircleChart totalBudget={8000} spent={5950} />
            </div>
          </div>

          <div className="w-full bg-white  p-5 rounded-3xl  shadow-xs">
            <SavingBarChart />
          </div>

        </div>
      </div>
    </>
  )
}