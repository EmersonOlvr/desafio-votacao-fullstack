import { Box, Button, DialogActions, DialogContent, FormControl, FormControlLabel, FormLabel, Grid, Radio, RadioGroup, TextField } from '@mui/material';
import { useSnackbar } from 'notistack';
import React, { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { useModal } from '../../context/ModalContext';
import SnackbarUtil from '../../utils/SnackbarUtil';
import { LoadingButton } from '@mui/lab';
import SendIcon from '@mui/icons-material/Send';
import VoteService from '../../services/VoteService';
import TextMaskCustom from '../TextMaskCustom';

interface FormData {
    cpf: string;
    vote: boolean;
}

interface TopicVoteModalProps {
    modalId: string;
    topicId: string;
    onSuccess: () => void;
}

const TopicVoteModal: React.FC<TopicVoteModalProps> = ({ modalId, topicId, onSuccess }) => {
    const { enqueueSnackbar, closeSnackbar } = useSnackbar();
    const { closeModal } = useModal();

    const { control, handleSubmit } = useForm<FormData>();
    const [isSubmitting, setSubmitting] = useState(false);

    const onSubmit = (data: FormData) => {
        setSubmitting(true);
        setTimeout(() => {
            VoteService.vote(topicId, data.cpf, data.vote)
                .then(() => {
                    enqueueSnackbar(
                        <span>Voto computado com sucesso!</span>,
                        { variant: 'success', action: (key) => SnackbarUtil.getCloseActionButton(key, closeSnackbar) }
                    );
                    onSuccess();
                })
                .catch((error) => {
                    let statusCode = error?.response?.status;
                    enqueueSnackbar(
                        <span>{[400, 403, 404, 409].includes(statusCode) ? error.response.data.message : "Desculpe, não foi possível computar o voto no momento"}</span>,
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
                            name="cpf"
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
                                    label="CPF do associado"
                                    placeholder="CPF do associado..."
                                    variant="standard"
                                    inputProps={{
                                        mask: ["000.000.000-00"],
                                    }}
                                    InputProps={{
                                        inputComponent: TextMaskCustom as any,
                                    }}
                                    fullWidth
                                />
                            )}
                        />
                    </FormControl>
                    <FormControl component="fieldset" fullWidth>
                        <FormLabel component="legend">Voto do associado</FormLabel>
                        <Controller
                            name="vote"
                            control={control}
                            rules={{
                                required: { value: true, message: "Por favor, selecione uma opção." },
                            }}
                            render={({ field, fieldState }) => (
                                <>
                                    <RadioGroup {...field} row>
                                        <FormControlLabel
                                            value={true}
                                            control={<Radio />}
                                            label="Favorável"
                                        />
                                        <FormControlLabel
                                            value={false}
                                            control={<Radio />}
                                            label="Contra"
                                        />
                                    </RadioGroup>
                                    {fieldState?.error && (
                                        <Box color="error.main" mt={1}>
                                            {fieldState?.error?.message}
                                        </Box>
                                    )}
                                </>
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
                    startIcon={<SendIcon />}
                    loading={isSubmitting}
                    loadingPosition="start"
                    disabled={isSubmitting}
                >
                    Computar voto
                </LoadingButton>
            </DialogActions>
        </form >
    );
};

export default TopicVoteModal;
