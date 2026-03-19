package org.xiaoyu.xchatmind.service;

import org.xiaoyu.xchatmind.agent.tools.Tool;

import java.util.List;

public interface ToolFacadeService {
    List<Tool> getAllTools();

    List<Tool> getFixedTools();

    List<Tool> getOptionalTools();
}
