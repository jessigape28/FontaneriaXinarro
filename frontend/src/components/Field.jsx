export default function Field({ label, htmlFor, className = '', children }) {
    return (
        <div className={className}>
            <label htmlFor={htmlFor} className="mb-1 block text-sm font-medium text-slate-700">
                {label}
            </label>
            {children}
        </div>
    );
}