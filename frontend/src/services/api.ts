import axios from 'axios';
import type { GridFilterItem } from '@mui/x-data-grid';

export let organizationId: string | null = null;
export const setOrganizationId = (id: string | null) => {
    organizationId = id;
};

export interface Pagination {
    page?: number,
    size?: number,
}
export interface Ordination {
    orderBy?: string,
    order?: string | null,
}
export interface SearchParams {
    search?: string | null,
    filters?: GridFilterItem[],
}

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;