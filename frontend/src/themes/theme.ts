import { createTheme, type Theme } from '@mui/material/styles';
import { ptBR as dataGridPtBR } from '@mui/x-data-grid/locales';
import { ptBR as corePtBr } from '@mui/material/locale';

const theme: Theme = createTheme(
    {
        palette: {
            mode: 'light',
        },
    },
    dataGridPtBR, // x-data-grid translations
    corePtBr, // core translations,
);

export default theme;