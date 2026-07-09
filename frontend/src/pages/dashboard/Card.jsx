import {CARDS_STYLE} from "./dashboard.js";

export default function Card({title, amount = 0, variant = 'balance'}) {

  const styles = CARDS_STYLE;

  const currentStyle = styles[variant] || styles.balance;
  const IconComponent = currentStyle.Icon;

  return (
    <div
      className={`p-6 rounded-3xl shadow-sm flex-1 min-w-40 transition-all duration-200 ease-out hover:-translate-y-1 hover:shadow-md rel` + `ative ${currentStyle.bg}`}>

      {/* Top Right Logo/Icon Container */}
      <div className={`absolute top-5 right-5 p-2 rounded-xl flex items-center justify-center ${currentStyle.iconBg}`}>
        <IconComponent className="w-5 h-5"/>
      </div>

      <div className="flex flex-col justify-between h-full pt-2">
        <div>
          {/* Title Label */}
          <p className={`text-sm font-medium tracking-wide ${currentStyle.titleColor}`}>
            {title}
          </p>

          {/* Amount Display */}
          <h3 className={`text-3xl font-bold tracking-tight mt-2 md:text-4xl ${currentStyle.amountColor}`}>
            ₹{amount.toLocaleString('en-IN', {minimumFractionDigits: 2})}
          </h3>
        </div>
      </div>
    </div>
  );
}