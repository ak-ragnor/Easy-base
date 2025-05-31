import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'

// Add spinner CSS animation
const style = document.createElement('style');
style.textContent = `
@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

body {
    margin: 0;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
        'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
        sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    background-color: #f5f5f5;
}
`;
document.head.appendChild(style);

// Error Boundary Component
class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, error: null };
    }

    static getDerivedStateFromError(error) {
        return { hasError: true };
    }

    componentDidCatch(error, errorInfo) {
        this.setState({ error: error });
        console.error('React Error Boundary caught an error:', error, errorInfo);
    }

    render() {
        if (this.state.hasError) {
            return (
                <div style={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    minHeight: '100vh',
                    padding: '20px',
                    textAlign: 'center',
                    fontFamily: 'Arial, sans-serif'
                }}>
                    <h1 style={{ color: '#d32f2f', marginBottom: '16px' }}>
                        Application Error
                    </h1>
                    <p style={{ color: '#666', marginBottom: '24px', maxWidth: '600px' }}>
                        The application encountered an unexpected error.
                        Please refresh the page or contact support if the problem persists.
                    </p>
                    <button
                        onClick={() => window.location.reload()}
                        style={{
                            background: '#1976d2',
                            color: 'white',
                            border: 'none',
                            padding: '12px 24px',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            fontSize: '14px',
                            fontWeight: '500'
                        }}
                    >
                        Reload Application
                    </button>
                </div>
            );
        }

        return this.props.children;
    }
}

console.log('🚀 Starting Easy Base Enterprise React App...');

// Initialize React application
const container = document.getElementById('root');
if (!container) {
    console.error('Root container not found!');
} else {
    const root = ReactDOM.createRoot(container);

    try {
        root.render(
            <React.StrictMode>
                <ErrorBoundary>
                    <App />
                </ErrorBoundary>
            </React.StrictMode>
        );

        console.log('✅ React App rendered successfully');

        // Hide loading indicator after a short delay
        setTimeout(() => {
            if (window.hideLoadingIndicator) {
                window.hideLoadingIndicator();
                console.log('✅ Loading indicator hidden');
            }
        }, 500);

    } catch (error) {
        console.error('❌ Failed to render React App:', error);
    }
}