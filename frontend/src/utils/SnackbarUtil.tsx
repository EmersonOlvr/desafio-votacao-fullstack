import { Close as CloseIcon } from "@mui/icons-material";
import { IconButton } from "@mui/material";
import type { SnackbarKey } from "notistack";

export default class SnackbarUtil {

    static getCloseActionButton(key: SnackbarKey, closeSnackbar: (key?: SnackbarKey) => void): React.ReactNode {
        return (
            <IconButton size="small" onClick={() => closeSnackbar(key)} color="inherit">
                <CloseIcon fontSize="small" />
            </IconButton>
        );
    }

}