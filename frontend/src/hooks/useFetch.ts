import { useState, useEffect } from 'react';
import axios, { type AxiosResponse, type CancelTokenSource } from 'axios';
import useDeepCallback from './useDeepCallback';
import ObjectUtil from '../utils/ObjectUtil';

interface ApiResponse<T> {
    data: T;
    error: any | null;
    loading: boolean;
    refresh: () => void;
}

type FetchFunction = (cancelToken: CancelTokenSource) => Promise<AxiosResponse<any>>;

const useFetch = <T>(
    fetchFunction: FetchFunction,
    fetchDeps: React.DependencyList,
    config: {
        skipInitialFetch?: boolean,
        contentAttribute?: string,
        conditionToFetch?: boolean,
        delayToFetch?: number,
        initialValue?: any,
    } = {},
): ApiResponse<T> => {

    const {
        skipInitialFetch = false,
        contentAttribute,
        conditionToFetch = true,
        delayToFetch,
        initialValue,
    } = config;

    const [data, setData] = useState<T>(initialValue ?? undefined);
    const [loading, setLoading] = useState<boolean>(!skipInitialFetch); // inicia como false se skipInitialFetch for true
    const [error, setError] = useState<any | null>(null);
    const [trigger, setTrigger] = useState(0);
    const [hasFetched, setHasFetched] = useState(false);

    const memoizedFetchFunction = useDeepCallback(fetchFunction, fetchDeps);

    const refresh = () => setTrigger(prev => prev + 1);

    const request = (cancelTokenSource: CancelTokenSource) => {
        memoizedFetchFunction(cancelTokenSource)
            .then(response => {
                const extractedData = contentAttribute
                    ? (ObjectUtil.getValueByPath(response, contentAttribute) ?? [])
                    : response.data;

                setData(extractedData);
                setLoading(false);
                setHasFetched(true);
            })
            .catch(error => {
                if (!axios.isCancel(error)) {
                    setError(error);
                    setLoading(false);
                    setHasFetched(true);
                }
            });
    }

    useEffect(() => {
        if (!conditionToFetch || (skipInitialFetch && !hasFetched)) {
            setHasFetched(true); // marca que a requisição inicial foi "ignorada"
            return;
        }

        let cancelTokenSource: CancelTokenSource = axios.CancelToken.source();
        let timeoutId: ReturnType<typeof setTimeout> | null = null;

        setLoading(true);

        // aplica um delay caso haja
        if (delayToFetch)
            timeoutId = setTimeout(() => {
                request(cancelTokenSource);
            }, delayToFetch);
        else
            request(cancelTokenSource);

        return () => {
            cancelTokenSource.cancel('Nova requisição iniciada, cancelando a anterior.');
            if (timeoutId)
                clearTimeout(timeoutId);
        };
    }, [memoizedFetchFunction, contentAttribute, trigger]);

    return { data, loading, error, refresh };
};

export default useFetch;
