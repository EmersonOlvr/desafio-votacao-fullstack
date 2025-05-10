import type { AxiosResponse, CancelTokenSource } from 'axios';
import api from './api';

class VoteService {

    private apiVersion: string = "v1";

    vote(topicId: string, cpf: string, vote: boolean, cancelTokenSource?: CancelTokenSource) {
        return api.post<any>(`${this.apiVersion}/vote/topic/${topicId}`, null, { params: { cpf, vote }, cancelToken: cancelTokenSource?.token });
    }

    getResults(topicId: string, cancelTokenSource?: CancelTokenSource): Promise<AxiosResponse<any, any>> {
        return api.get<any>(`${this.apiVersion}/vote/topic/${topicId}/results`, {  cancelToken: cancelTokenSource?.token });
    }

}

export default new VoteService();