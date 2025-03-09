package cn.magic.web.board.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.board.entity.Board;
import cn.magic.web.board.entity.BoardParam;
import cn.magic.web.board.service.BoardService;
import cn.magic.web.role.entity.Role;
import cn.magic.web.sys_user.entity.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    @Autowired
    BoardService boardService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Board board) {
        if (boardService.save(board)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Board board) {
        if (boardService.updateById(board)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (boardService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    @GetMapping("/list")
    public ResultVo getAllList(BoardParam param){
        //构造分页对象
        IPage<Board> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<Board> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getName())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(Board::getName, param.getName());
        }
        //查询
        IPage<Board> list = boardService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

}
