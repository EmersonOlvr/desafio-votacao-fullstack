export interface TopicWithOpenSessionDto {
    id: string,
    title: string,
    description: string,
    createdAt: string,
    sessionId: string,
    sessionStart: string,
    sessionEnd: string
}
export type Result = "FAVORABLE" | "AGAINST" | "TIED";
export interface TopicVoteResults {
    uuid: string,
    title: string,
    description: string,
    createdAt: string,

    favorableVotes: number,
    againstVotes: number,
    currentResult: Result,
    finalResult: Result,

    currentResultText: string,
    finalResultText: string,
}