import React from 'react';
import DialogTitle from '@mui/material/DialogTitle';
import { type DialogTitleProps as MuiDialogTitleProps } from '@mui/material/DialogTitle';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';

export interface DialogTitleProps extends MuiDialogTitleProps {
    children?: React.ReactNode;
    onClose: () => void;
}

const ModalTitle: React.FC<DialogTitleProps> = ({ ...props }) => {
    const { children, onClose, ...other } = props;

    return (
        <DialogTitle sx={{ m: 0, p: 2 }} {...other}>
            {children}
            {onClose ? (
                <IconButton
                    aria-label="close"
                    onClick={onClose}
                    sx={{
                        position: 'absolute',
                        right: 8,
                        top: 8,
                        color: (theme) => theme.palette.grey[500],
                    }}
                >
                    <CloseIcon />
                </IconButton>
            ) : null}
        </DialogTitle>
    );
}

export default ModalTitle;