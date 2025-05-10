import '@fontsource/roboto/300.css';
import '@fontsource/roboto/300-italic.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/400-italic.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/500-italic.css';
import '@fontsource/roboto/700.css';
import '@fontsource/roboto/700-italic.css';

import { SnackbarProvider } from 'notistack'
import './App.css'
import TopicListPage from './pages/TopicListPage'
import { ModalProvider } from './context/ModalContext'

function App() {
    return (
        <>
            <SnackbarProvider
                maxSnack={3}
                autoHideDuration={5000}
                anchorOrigin={{
                    vertical: 'top',
                    horizontal: "right",
                }}
            >
                <ModalProvider>
                    <TopicListPage />
                </ModalProvider>
            </SnackbarProvider>
        </>
    )
}

export default App
