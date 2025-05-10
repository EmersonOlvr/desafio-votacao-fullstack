import { Box, Chip, Container, IconButton, Tooltip } from "@mui/material";
import { DataGrid, type GridColDef, type GridPaginationModel, type GridRenderCellParams, type GridSortModel } from "@mui/x-data-grid";
import PollIcon from '@mui/icons-material/Poll';
import PlayArrowIcon from "@mui/icons-material/PlayArrow";
import ThumbsUpDownIcon from '@mui/icons-material/ThumbsUpDown';
import PollOutlinedIcon from '@mui/icons-material/PollOutlined';
import type { TopicWithOpenSessionDto } from "../types/Topic";
import TopicService from "../services/TopicService";
import useFetch from "../hooks/useFetch";
import React from "react";
import { useModal } from "../context/ModalContext";
import TopicStartSessionModal from "../components/TopicStartSessionModal";
import Card from "../components/Card";
import CrudHeader from "../components/CrudHeader";
import TopicCreateModal from "../components/TopicCreateModal";
import TopicVoteModal from "../components/TopicVoteModal";
import DataGridUtil from "../utils/DataGridUtil";
import moment from 'moment';
import TopicResultsModal from "../components/TopicResultsModal";

interface TopicsPage {
    content: TopicWithOpenSessionDto[];
    totalElements: number;
}

const TopicListPage: React.FC = ({ }) => {

    const { openModal, closeModal } = useModal();

    // paginação, ordenação e filtros
    const [paginationModel, setPaginationModel] = React.useState<GridPaginationModel>({
        pageSize: 10,
        page: 0,
    });
    const [sortModel, setSortModel] = React.useState<GridSortModel>([]);

    const { data: topics, loading: isLoadingTopics, refresh: refreshTopics } = useFetch<TopicsPage>((cancelToken) =>
        TopicService.list(
            { page: paginationModel.page + 1, size: paginationModel.pageSize },
            { orderBy: sortModel[0]?.field, order: sortModel[0]?.sort },
            cancelToken
        ),
        [paginationModel, sortModel],
        { initialValue: [], contentAttribute: 'data', delayToFetch: 500 },
    );

    const handleCreate = () => {
        let modalId = "modal-topic-create";
        openModal({
            id: modalId,
            title: "Criar pauta",
            content: (
                <TopicCreateModal
                    modalId={modalId}
                    onSuccess={() => {
                        closeModal(modalId);
                        refreshTopics();
                    }}
                />
            ),
        });
    }

    const handleStartSession = (topicId: string) => {
        let modalId = "modal-topic-start-session";
        openModal({
            id: modalId,
            title: "Iniciar sessão de votação",
            content: (
                <TopicStartSessionModal
                    modalId={modalId}
                    topicId={topicId}
                    onSuccess={() => {
                        closeModal(modalId);
                        refreshTopics();
                    }}
                />
            ),
        });
    };

    const handleVote = (topicId: string, topicTitle: string) => {
        let modalId = "modal-topic-vote";
        openModal({
            id: modalId,
            title: `Votar na pauta "${topicTitle}"`,
            content: (
                <TopicVoteModal
                    modalId={modalId}
                    topicId={topicId}
                    onSuccess={() => {
                        closeModal(modalId);
                    }}
                />
            ),
        });
    };

    const handleGetResults = (topicId: string, topicTitle: string) => {
        let modalId = "modal-topic-results";
        openModal({
            id: modalId,
            title: `Resultado da pauta "${topicTitle}"`,
            content: (
                <TopicResultsModal
                    modalId={modalId}
                    topicId={topicId}
                />
            ),
        });
    }

    const columns: GridColDef[] = [
        { field: "id", headerName: "ID", flex: 1, filterable: false, },
        { field: "title", headerName: "Título", flex: 1, minWidth: 150, filterable: false, },
        { field: "description", headerName: "Descrição", flex: 2, minWidth: 300, filterable: false, },
        {
            field: "session",
            headerName: "Status da sessão de votação atual",
            flex: 2,
            minWidth: 250,
            sortable: false, 
            filterable: false,
            renderCell: (params: GridRenderCellParams) => {
                const dataAtual = new Date();
                return params.row.sessionId
                    ? (
                        new Date(params.row.sessionEndTime).getTime() > dataAtual.getTime()
                            ? <Chip label="Sessão em andamento" color="warning" />
                            : <Chip label="Sessão finalizada" color="info" />
                    )
                    : <Chip label="Nenhuma sessão iniciada" />
            },
        },
        {
            field: "sessionEndTime",
            headerName: "Finalização da sessão",
            flex: 1,
            minWidth: 220,
            sortable: false, 
            filterable: false,
            valueFormatter: (value) => value ? moment(value).format('DD/MM/yyyy HH:mm:ss') : DataGridUtil.formatValue(value),
        },
        {
            field: "actions",
            align: "right",
            headerName: "Ações",
            sortable: false,
            filterable: false,
            renderCell: (params: GridRenderCellParams) => {
                const dataAtual = new Date();
                return (
                    <Box>
                        {(!params.row.sessionId || new Date(params.row.sessionEndTime).getTime() < dataAtual.getTime()) && (
                            <Tooltip title="Iniciar sessão de votação">
                                <span>
                                    <IconButton color="success" onClick={() => handleStartSession(params.row.id)}>
                                        <PlayArrowIcon fontSize="inherit" />
                                    </IconButton>
                                </span>
                            </Tooltip>
                        )}
                        {(params.row.sessionId && new Date(params.row.sessionEndTime).getTime() > dataAtual.getTime()) && (
                            <Tooltip title="Votar">
                                <span>
                                    <IconButton onClick={() => handleVote(params.row.id, params.row.title)}>
                                        <ThumbsUpDownIcon fontSize="inherit" />
                                    </IconButton>
                                </span>
                            </Tooltip>
                        )}
                        {(params.row.sessionId) && (
                            <Tooltip title="Resultado das votações">
                                <span>
                                    <IconButton onClick={() => handleGetResults(params.row.id, params.row.title)}>
                                        <PollOutlinedIcon fontSize="inherit" />
                                    </IconButton>
                                </span>
                            </Tooltip>
                        )}
                    </Box>
                )
            },
        },
    ];

    return (
        <Container>
            <Card>
                <CrudHeader
                    titleIcon={<PollIcon />}
                    title="Pautas"
                    addButtonTitle="Adicionar pauta"
                    AddButtonProps={{
                        onClick: handleCreate,
                    }}
                />
                <DataGrid
                    pagination
                    paginationMode="server"
                    paginationModel={paginationModel}
                    onPaginationModelChange={setPaginationModel}
                    pageSizeOptions={[5, 10, 50, 100]}
                    sortingMode="server"
                    sortModel={sortModel}
                    onSortModelChange={(model) => setSortModel(model)}
                    initialState={{
                        columns: {
                            columnVisibilityModel: {
                                id: false,
                            },
                        }
                    }}
                    columns={columns}
                    rows={topics.content}
                    rowCount={topics.totalElements}
                    loading={isLoadingTopics}
                    getRowId={(row) => row.id}
                    autoHeight
                    disableRowSelectionOnClick
                />
            </Card>
        </Container>
    );
}

export default TopicListPage;