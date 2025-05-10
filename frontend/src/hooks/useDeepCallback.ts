import { useCallback, useRef } from 'react';
import { isEqual } from 'lodash';

function useDeepCallback<T extends (...args: any[]) => any>(callback: T, deps: ReadonlyArray<any>): T {
    const lastDeps = useRef<ReadonlyArray<any>>(deps);

    const isSame = lastDeps.current.every((dep, index) => isEqual(dep, deps[index]));

    if (!isSame) {
        lastDeps.current = deps;
    }

    return useCallback(callback, lastDeps.current);
}

export default useDeepCallback;