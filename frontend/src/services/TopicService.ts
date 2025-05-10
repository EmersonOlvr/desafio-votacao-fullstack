import type { AxiosResponse, CancelTokenSource } from 'axios';
import api, { type Ordination, type Pagination } from './api';

class TopicService {

    private apiVersion: string = "v1";

    list(pagination?: Pagination, ordination?: Ordination, cancelTokenSource?: CancelTokenSource): Promise<AxiosResponse<any, any>> {
        return api.get<any>(`${this.apiVersion}/topic/list`, { params: { ...pagination, ...ordination }, cancelToken: cancelTokenSource?.token });
    }

    create(title: string, description: string, cancelTokenSource?: CancelTokenSource) {
        return api.post<any>(`${this.apiVersion}/topic`, { title, description }, { cancelToken: cancelTokenSource?.token });
    }

    startVotingSession(topicId: string, durationInMinutes?: number, cancelTokenSource?: CancelTokenSource) {
        return api.post<any>(`${this.apiVersion}/topic/${topicId}/startVotingSession`, null, { params: { durationInMinutes }, cancelToken: cancelTokenSource?.token });
    }

}

export default new TopicService();