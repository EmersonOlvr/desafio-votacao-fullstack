import React, { type JSX, type ReactNode } from 'react';
import { styled } from '@mui/material/styles';
import Dialog, { type DialogProps } from '@mui/material/Dialog';
import ModalTitle from './ModalTitle';
import { Modal as MuiModal, Typography } from '@mui/material';

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
    '& .MuiDialogContent-root': {
        padding: theme.spacing(2),
    },
    '& .MuiPaper-root': {
        borderRadius: "10px",
    },
    '& .MuiDialogActions-root': {
        padding: theme.spacing(1),
    },
}));

interface ModalWrapperProps {
    open: boolean;
    onClose: () => void;
    children: React.ReactNode;
}
const ModalWrapper: React.FC<ModalWrapperProps> = ({ open, onClose, children }) => {
    return (
        <MuiModal
            open={open}
            onClose={onClose}
        >
            <div>
                {open ? children : null}
            </div>
        </MuiModal>
    );
};
export interface ModalProps extends Omit<DialogProps, 'title'> {
    titleIcon?: JSX.Element,
    title: ReactNode;
    open: boolean;
    children?: React.ReactNode;
    onClose: (reason: 'backdropClick' | 'escapeKeyDown' | 'buttonClick' | 'cancel') => void;
}
const Modal: React.FC<ModalProps> = ({ titleIcon, title, open, children, onClose, ...props }) => {

    return (
        <ModalWrapper
            open={open}
            onClose={() => onClose('backdropClick')}
        >
            <BootstrapDialog
                open={open}
                onClose={(_e, reason) => onClose(reason)}
                hideBackdrop
                closeAfterTransition
                {...props}
            >
                <ModalTitle onClose={() => onClose('buttonClick')}>
                    {titleIcon && <Typography component="span" fontSize="1.2rem" sx={{ position: "relative", top: "2px", mr: 1 }}>{titleIcon}</Typography>}
                    {title}
                </ModalTitle>
                {children}
            </BootstrapDialog>
        </ModalWrapper>
    );
}

export default Modal;