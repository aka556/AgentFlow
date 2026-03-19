package org.xiaoyu.xchatmind.service.Impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xiaoyu.xchatmind.agent.tools.Tool;
import org.xiaoyu.xchatmind.agent.tools.ToolType;
import org.xiaoyu.xchatmind.service.ToolFacadeService;

import java.util.List;

@Service
@AllArgsConstructor
public class ToolFacadeServiceImpl implements ToolFacadeService {

    private final List<Tool> tools;

    @Override
    public List<Tool> getAllTools() {
        return tools;
    }

    @Override
    public List<Tool> getFixedTools() {
        return getToolsByType(ToolType.FIXED);
    }

    @Override
    public List<Tool> getOptionalTools() {
        return getToolsByType(ToolType.OPERATIONAL);
    }

    private List<Tool> getToolsByType(ToolType type) {
        return tools.stream()
                .filter(tool -> tool.getType().equals(type))
                .toList();
    }
}
