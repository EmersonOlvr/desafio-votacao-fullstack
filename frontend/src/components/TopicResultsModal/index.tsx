import { Button, DialogActions, DialogContent } from '@mui/material';
import React from 'react';
import { useModal } from '../../context/ModalContext';
import useFetch from '../../hooks/useFetch';
import VoteService from '../../services/VoteService';
import type { TopicVoteResults } from '../../types/Topic';
import { Refresh } from '@mui/icons-material';

interface TopicResultsModalProps {
    modalId: string;
    topicId: string;
}

const TopicResultsModal: React.FC<TopicResultsModalProps> = ({ modalId, topicId }) => {

    const { closeModal } = useModal();

    const { data: results, loading: isLoadingResults, refresh: refreshResults } = useFetch<TopicVoteResults>((cancelToken) =>
        VoteService.getResults(topicId, cancelToken),
        [],
        { initialValue: [], delayToFetch: 500 },
    );

    return (
        <>
            <DialogContent sx={{ pt: "0px!important" }}>
                <div>
                    <p><strong>Votos favoráveis:</strong> {isLoadingResults ? "..." : results.favorableVotes}</p>
                    <p><strong>Votos contra:</strong> {isLoadingResults ? "..." : results.againstVotes}</p>

                    <p><strong>Resultado atual:</strong> {isLoadingResults ? "..." : results.currentResultText}</p>
                    <p><strong>Resultado final:</strong> {isLoadingResults ? "..." : results.finalResultText}</p>

                    <Button
                        variant="outlined" startIcon={<Refresh />}
                        size="small"
                        onClick={refreshResults}
                        disabled={isLoadingResults}
                    >
                        Atualizar resultado
                    </Button>
                </div>
            </DialogContent>
            <DialogActions>
                <Button
                    color="inherit"
                    onClick={() => closeModal(modalId)}
                    autoFocus
                >
                    Fechar
                </Button>
            </DialogActions>
        </>
    );
};

export default TopicResultsModal;
