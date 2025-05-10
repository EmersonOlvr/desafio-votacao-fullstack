import { Button, DialogActions, DialogContent, FormControl, Grid, TextField } from '@mui/material';
import { useSnackbar } from 'notistack';
import React, { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { useModal } from '../../context/ModalContext';
import TopicService from '../../services/TopicService';
import SnackbarUtil from '../../utils/SnackbarUtil';
import { LoadingButton } from '@mui/lab';
import { Add as AddIcon } from '@mui/icons-material';

interface FormData {
    title: string;
    description: string;
}

interface TopicCreateModalProps {
    modalId: string;
    onSuccess: () => void;
}

const TopicCreateModal: React.FC<TopicCreateModalProps> = ({ modalId, onSuccess }) => {
    const { enqueueSnackbar, closeSnackbar } = useSnackbar();
    const { closeModal } = useModal();

    const { control, handleSubmit } = useForm<FormData>();
    const [isSubmitting, setSubmitting] = useState(false);

    const onSubmit = (data: FormData) => {
        setSubmitting(true);
        setTimeout(() => {
            TopicService.create(data.title, data.description)
                .then(() => {
                    enqueueSnackbar(
                        <span>Pauta criada com sucesso!</span>,
                        { variant: 'success', action: (key) => SnackbarUtil.getCloseActionButton(key, closeSnackbar) }
                    );
                    onSuccess();
                })
                .catch((error) => {
                    let statusCode = error?.response?.status;
                    enqueueSnackbar(
                        <span>{[400, 403, 404, 409].includes(statusCode) ? error.response.data.message : "Desculpe, não foi possível criar a pauta no momento"}</span>,
                        { variant: 'error', action: (key) => SnackbarUtil.getCloseActionButton(key, closeSnackbar) }
                    );
                })
                .finally(() => setSubmitting(false));
        }, 500);
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <DialogContent sx={{ pt: "0px!important" }}>
                <Grid container spacing={2}>
                    <FormControl fullWidth>
                        <Controller
                            name="title"
                            control={control}
                            rules={{
                                required: { value: true, message: "Este campo é obrigatório." },
                                maxLength: { value: 50, message: "O título deve ter no máximo 50 caracteres." },
                            }}
                            defaultValue=""
                            render={({ field, fieldState }) => (
                                <TextField
                                    {...field}
                                    error={!!fieldState.error}
                                    helperText={fieldState.error?.message}
                                    id="title"
                                    label="Título da pauta"
                                    placeholder="Título da pauta..."
                                    variant="standard"
                                    fullWidth
                                />
                            )}
                        />
                    </FormControl>
                    <FormControl fullWidth>
                        <Controller
                            name="description"
                            control={control}
                            rules={{
                                required: { value: true, message: "Este campo é obrigatório." },
                                maxLength: { value: 50, message: "A descrição deve ter no máximo 500 caracteres." },
                            }}
                            defaultValue=""
                            render={({ field, fieldState }) => (
                                <TextField
                                    {...field}
                                    error={!!fieldState.error}
                                    helperText={fieldState.error?.message}
                                    id="title"
                                    label="Descrição da pauta"
                                    placeholder="Descrição da pauta..."
                                    variant="standard"
                                    fullWidth
                                />
                            )}
                        />
                    </FormControl>
                </Grid>
            </DialogContent>
            <DialogActions>
                <Button
                    color="inherit"
                    onClick={() => closeModal(modalId)}
                    disabled={isSubmitting}
                    autoFocus
                >
                    Cancelar
                </Button>
                <LoadingButton
                    type="submit"
                    color="success"
                    startIcon={<AddIcon />}
                    loading={isSubmitting}
                    loadingPosition="start"
                    disabled={isSubmitting}
                >
                    Adicionar pauta
                </LoadingButton>
            </DialogActions>
        </form >
    );
};

export default TopicCreateModal;
