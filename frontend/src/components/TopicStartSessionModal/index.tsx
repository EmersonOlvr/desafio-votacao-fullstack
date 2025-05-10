import { Button, DialogActions, DialogContent, TextField } from '@mui/material';
import { useSnackbar } from 'notistack';
import React, { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { useModal } from '../../context/ModalContext';
import PlayArrowIcon from "@mui/icons-material/PlayArrow";
import TopicService from '../../services/TopicService';
import SnackbarUtil from '../../utils/SnackbarUtil';
import { LoadingButton } from '@mui/lab';

interface FormData {
    durationInMinutes?: number;
}

interface TopicStartSessionModalProps {
    modalId: string;
    topicId: string;
    onSuccess: () => void;
}

const TopicStartSessionModal: React.FC<TopicStartSessionModalProps> = ({ modalId, topicId, onSuccess }) => {
    const { enqueueSnackbar, closeSnackbar } = useSnackbar();
    const { closeModal } = useModal();

    const { control, handleSubmit } = useForm<FormData>();
    const [isSubmitting, setSubmitting] = useState(false);

    const onSubmit = async (data: FormData) => {
        setSubmitting(true);
        setTimeout(() => {
            TopicService.startVotingSession(topicId, data.durationInMinutes)
                .then(() => {
                    enqueueSnackbar(
                        <span>Sessão de votação iniciada!</span>,
                        { variant: 'success', action: (key) => SnackbarUtil.getCloseActionButton(key, closeSnackbar) }
                    );
                    onSuccess();
                })
                .catch((error) => {
                    let statusCode = error?.response?.status;
                    enqueueSnackbar(
                        <span>{[400, 403, 404, 409].includes(statusCode) ? error.response.data.message : "Desculpe, não foi possível iniciar a sessão no momento"}</span>,
                        { variant: 'error', action: (key) => SnackbarUtil.getCloseActionButton(key, closeSnackbar) }
                    );
                })
                .finally(() => setSubmitting(false));
        }, 500);
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <DialogContent sx={{ pt: "0px!important" }}>
                <Controller
                    name="durationInMinutes"
                    control={control}
                    render={({ field }) => (
                        <TextField
                            {...field}
                            id="duration-in-minutes"
                            label="Duração da sessão em minutos"
                            defaultValue={null}
                            placeholder="Ex.: 5"
                            type="number"
                            variant="standard"
                            helperText="Caso nenhum valor seja especificado, por padrão a sessão ficará aberta por 1 minuto"
                            fullWidth
                        />
                    )}
                />
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
                    startIcon={<PlayArrowIcon />}
                    loading={isSubmitting}
                    loadingPosition="start"
                    disabled={isSubmitting}
                >
                    Iniciar sessão
                </LoadingButton>
            </DialogActions>
        </form>
    );
};

export default TopicStartSessionModal;
