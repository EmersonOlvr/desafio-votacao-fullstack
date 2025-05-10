import React, { createContext, useState, useContext, type ReactNode } from 'react';
import Modal from '../components/Modal';
import { v4 as uuidv4 } from 'uuid';
import type { ModalProps } from '@mui/material';

interface ModalContextType {
    openModal: (modal: ModalInstance) => void;
    closeModal: (id: string) => void;
    closeAllModals: () => void;
}

const ModalContext = createContext<ModalContextType | undefined>(undefined);

interface BaseModalInstance extends Omit<ModalProps, 'children' | 'content' | 'onClose' | 'open' | 'title'> {
    id?: string;
    content: ReactNode;
    useCustomModal?: boolean;
}
interface ModalWithCustom extends BaseModalInstance {
    useCustomModal: true;
    title?: ReactNode; // title opcional quando useCustomModal é true
}
interface ModalWithoutCustom extends BaseModalInstance {
    useCustomModal?: false;
    title: ReactNode; // title obrigatório quando useCustomModal é false ou undefined
}
type ModalInstance = ModalWithCustom | ModalWithoutCustom;

export const ModalProvider = ({ children }: { children: ReactNode }) => {
    const [modals, setModals] = useState<ModalInstance[]>([]);

    const openModal = (modal: ModalInstance) => {
        if (!modal.id)
            modal.id = uuidv4();

        setModals((prev) => [...prev, modal]);
    };

    const closeModal = (id: string) => {
        setModals((prev) => prev.filter((modal) => modal.id !== id));
    };

    const closeAllModals = () => {
        setModals([]);
    };

    return (
        <ModalContext.Provider value={{ openModal, closeModal, closeAllModals }}>
            {children}
            {modals.map((modal, index) => {
                const { content, title, ...modalProps } = modal;
                return (
                    !modal.useCustomModal ? (
                        <Modal
                            key={index}
                            open
                            // style={{ zIndex: 1300 + index }}
                            onClose={() => closeModal(modal.id!)}
                            fullWidth
                            title={title}
                            {...modalProps}
                        >
                            {modal.content}
                        </Modal>
                    ) : (
                        <React.Fragment key={index}>
                            {modal.content}
                        </React.Fragment>
                    )
                )
            })}
        </ModalContext.Provider>
    );
};

export const useModal = () => {
    const context = useContext(ModalContext);
    if (!context) {
        throw new Error('useModal must be used within a ModalProvider');
    }
    return context;
};
