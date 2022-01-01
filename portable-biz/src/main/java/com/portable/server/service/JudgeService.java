package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.ServiceVerifyCode;
import com.portable.server.model.response.judge.HeartbeatResponse;
import com.portable.server.model.response.judge.SolutionInfoResponse;
import com.portable.server.type.SolutionStatusType;

import java.io.File;
import java.util.Map;

/**
 * @author shiroha
 */
public interface JudgeService {

    /**
     * 提交一个 judge 任务
     *
     * @param solutionId 目标 solution 的 ID
     * @throws PortableException 提供到 solution ID 是不存在时抛出
     */
    void addJudgeTask(Long solutionId) throws PortableException;

    /**
     * 提交一个 test 任务
     *
     * @param problemId 目标 solution 的 ID
     */
    void addTestTask(Long problemId);

    /**
     * 主动打断一个 judge 任务
     *
     * @param solutionId 目标 solution 的 ID
     */
    void killJudgeTask(Long solutionId);

    /**
     * 获取当前的设备码
     * <p>
     * 通常设备码是一个实时随机生成的值，且有一定的时效性，超过一段时间后将会失效
     * <p>
     * 但也可以通过环境变量强制指定固定的值用于部分解决方案
     * </p>
     *
     * @return 设备码
     */
    ServiceVerifyCode getServiceCode();

    /**
     * 注册设备
     *
     * @param serverCode      注册的设备码，用于校验是否是合法的 Judge
     * @param maxThreadCore   最大线程池数量
     * @param maxSocketCore   最大网络连接池数量
     * @param languageVersion 所有的语言版本信息
     * @return 返回注册成功后为其分配的编号
     * @throws PortableException serverCode 错误则抛出
     */
    String registerJudge(String serverCode, Integer maxThreadCore, Integer maxSocketCore, Map<String, String> languageVersion) throws PortableException;

    /**
     * 新增加一个 TCP 连接
     *
     * @param judgeCode 被分配的编号
     * @throws PortableException judgeCode 错误则抛出
     */
    void append(String judgeCode) throws PortableException;

    /**
     * 心跳包
     *
     * @param socketAccumulation 当前的 socket 堆积任务数量
     * @param threadAccumulation 当前线程池堆积任务数量
     * @return 拉取的任务
     * @throws PortableException 当前未记录此连接则抛出
     */
    HeartbeatResponse heartBeat(Integer socketAccumulation, Integer threadAccumulation) throws PortableException;

    /**
     * 获取提交的信息
     *
     * @param solutionId 提交的 ID
     * @return 提交的信息
     * @throws PortableException 遇到意料之外的情况则抛出错误
     */
    SolutionInfoResponse getSolutionInfo(Long solutionId) throws PortableException;

    /**
     * 获取提交的代码
     *
     * @param solutionId 提交的 ID
     * @return 提交的代码内容
     * @throws PortableException 遇到意料之外的情况则抛出错误
     */
    String getSolutionCode(Long solutionId) throws PortableException;

    /**
     * 获取题目的 DIY Judge 代码
     *
     * @param problemId 题目的 ID
     * @return 题目的 DIY judge 代码
     * @throws PortableException 遇到意料之外的情况则抛出错误
     */
    String getProblemJudgeCode(Long problemId) throws PortableException;

    /**
     * 提交编译结果
     *
     * @param solutionId    对应的提交 ID
     * @param compileResult 编译结果（true -> 通过）
     * @param compileMsg    编译信息
     * @throws PortableException 遇到意料之外的情况则抛出错误
     */
    void reportCompileResult(Long solutionId, Boolean compileResult, String compileMsg) throws PortableException;

    /**
     * 获取运行结果
     *
     * @param solutionId 对应的提交 ID
     * @param statusType 状态
     * @param timeCost   耗时
     * @param memoryCost 内存消耗
     * @throws PortableException 遇到意料之外的情况则抛出错误
     */
    void reportRunningResult(Long solutionId, SolutionStatusType statusType, Integer timeCost, Integer memoryCost) throws PortableException;

    /**
     * 获取默认的标准 judge 列表
     *
     * @return 标准 judge 列表，用空格隔开
     */
    String getStandardJudgeList();

    /**
     * 获取默认的标准 judge 代码
     *
     * @param name 需要的 judge 代码名称
     * @return 标准代码任务
     */
    String getStandardJudgeCode(String name);

    /**
     * 获取题目的标准输入名称
     *
     * @param name 输入名称
     * @return 输入的文件
     */
    File getProblemInputTest(String name);

    /**
     * 获取题目的标准输出名称
     *
     * @param name 输出名称
     * @return 输出的文件
     */
    File getProblemOutputTest(String name);

    /**
     * 获取下一组测试数据的名称
     *
     * @param solutionId 提交的 ID
     * @return 测试数据的名称
     */
    String getSolutionNextTestName(Long solutionId);
}
