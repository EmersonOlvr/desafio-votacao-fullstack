export default class DataGridUtil {

    static formatValue(value: any): any {
        return value === null || value === undefined ? '—' : value;
    }

}