package org.xiaoyu.xchatmind.agent;

/**
 * @author xiaoyu
 * @desciption LLM状态
 */
public enum AgentState {
    IDLE, // 空闲
    PLANNING, // 规划中
    THINKING, // 思考中
    EXECUTING, // 执行中
    FINISHED, // 正常完成
    ERROR // 错误
}
