package cn.magic.web.board.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.board.entity.Board;
import cn.magic.web.board.entity.BoardParam;
import cn.magic.web.board.service.BoardService;
import cn.magic.web.post.entity.Post;
import cn.magic.web.post.service.PostService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private PostService postService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Board board) {
        //当字段值为空字符串时，MyBatis-Plus会将其作为有效值插入，覆盖数据库默认值。需确保未传递的字段保持为 null 而非空字符串。
        if (board.getIcon().equals(""))
            board.setIcon(null);
        if (board.getSortOrder() > 10 || board.getSortOrder() < 1)
            board.setSortOrder(null);
        System.out.println(board.getCreatorId());
        if (boardService.save(board)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    // 申请
    @PostMapping("/apply")
    public ResultVo apply(@RequestBody Board board) {
        //当字段值为空字符串时，MyBatis-Plus会将其作为有效值插入，覆盖数据库默认值。需确保未传递的字段保持为 null 而非空字符串。
        if (board.getIcon().equals(""))
            board.setIcon(null);
        board.setStatus("pending");
        board.setSortOrder(null);
        board.setIsDeleted(null);
        if (boardService.save(board)) {
            return ResultVo.success("申请请求成功!");
        }
        return ResultVo.error("申请失败!");
    }

    // 编辑
    @PutMapping
    public ResultVo edit(@RequestBody Board board) {
        if (boardService.updateById(board)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    // 直接删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (boardService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    // 软删除
    @Transactional
    @DeleteMapping("/del/{id}")
    public ResultVo deleteByTag(@PathVariable("id") Long id) {
        Board board = boardService.getById(id);
        if (board != null) {
            board.setIsDeleted(true);
            boardService.updateById(board);
            return ResultVo.success("软删除成功!");
        } else
            return ResultVo.error("删除失败!");
    }

    //分页获取审核通过的顶级帖子
    @GetMapping("/page")
    public ResultVo getList(BoardParam param) {
        //构造分页对象
        IPage<Board> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.in("status", Arrays.asList("active", "banned"));
        query.eq("is_deleted", 0).eq("parent_id", null);
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getName())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(Board::getName, param.getName()).orderByAsc(Board::getSortOrder);
        } else query.lambda().orderByAsc(Board::getSortOrder);
        //查询
        IPage<Board> list = boardService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

    // 获取完整的树形数据
    @GetMapping("/tree")
    public ResultVo getTree() {
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.in("status", Arrays.asList("active", "banned"));
        query.lambda().eq(Board::getIsDeleted, 0)
                .orderByAsc(Board::getSortOrder);
        List<Board> boardList = boardService.list(query);
        // 构建树形数据
        Map<Long, Board> boardMap = new LinkedHashMap<>();
        // 先处理父节点
        boardList.stream().filter(board -> board.getParentId() == null)
                .sorted(Comparator.comparing(Board::getSortOrder))
                .forEach(board -> {
                    boardMap.put(board.getBoardId(), board);
                    board.setChildren(new ArrayList<>());
                });
        // 处理子节点
        boardList.stream()
                .filter(board -> board.getParentId() != null)
                .sorted(Comparator.comparing(Board::getSortOrder))
                .forEach(board -> {
                    Board parent = boardMap.get(board.getParentId());
                    if (parent != null) {
                        parent.getChildren().add(board);
                    }
                });

        List<Board> boardTree = new ArrayList<>(boardMap.values());
        return ResultVo.success("查询树形数据成功", boardTree);
    }

    // 获取搜索的树形数据
    @GetMapping("/searchTree")
    public ResultVo getSearchTree(String name) {
        // 1. 查询匹配的顶级版区
        List<Board> matchedTopBoards = boardService.searchTopBoardTree(name);
        if (matchedTopBoards.isEmpty()) {
            return ResultVo.success("未找到匹配的顶级版区", Collections.emptyList());
        }

        // 2. 获取所有后代节点
        List<Long> topIds = matchedTopBoards.stream()
                .map(Board::getBoardId)
                .collect(Collectors.toList());
        List<Board> allRelatedBoards = boardService.getAllDescendants(topIds);

        // 3. 合并所有节点（保持查询顺序）
        List<Board> allBoards = new ArrayList<>(matchedTopBoards);
        allBoards.addAll(allRelatedBoards);
        // 4. 构建树结构（同之前逻辑）
        Map<Long, Board> nodeMap = new LinkedHashMap<>();
        allBoards.forEach(b -> {
            Board node = new Board();
            BeanUtils.copyProperties(b, node);
            node.setChildren(new ArrayList<>());
            nodeMap.put(b.getBoardId(), node);
        });
        List<Board> resultTree = new ArrayList<>();
        nodeMap.values().forEach(node -> {
            if (node.getParentId() == null) {
                resultTree.add(node);
            } else {
                Board parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        });
        // 5. 排序
        sortTree(resultTree);
        // 6. 遍历顶级版区获取post数量
        for(Board board : resultTree){
            QueryWrapper<Post> query=new QueryWrapper<>();
            query.lambda().eq(Post::getStatus,"normal").eq(Post::getBoardId,board.getBoardId());
            Long postCount= postService.count(query);
            board.setPostCount(postCount);
        }
        return ResultVo.success("搜索成功", resultTree);
    }

    // 递归排序树结构
    private void sortTree(List<Board> nodes) {
        if (nodes == null) return;
        // 当前层排序
        nodes.sort(Comparator.comparingLong(Board::getSortOrder));
        // 递归子层
        nodes.forEach(n -> {
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                sortTree(n.getChildren());
            }
        });
    }

    //获取所有active顶级版块 集成搜索功能
    @GetMapping("/activeTopBoardList")
    public ResultVo getAllActiveList(@RequestParam(required = false) String name) {
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.lambda().eq(Board::getStatus, "active").isNull(Board::getParentId)
                .eq(Board::getIsDeleted, 0).orderByAsc(Board::getSortOrder);
        if (StringUtils.isNotEmpty(name)) {
            query.lambda().like(Board::getName, name);
        }
        List<Board> list = boardService.list(query);
        return ResultVo.success("查询活动的顶级版块成功", list);
    }

    //获取我的板块
    @GetMapping("/my/{userId}")
    public ResultVo getMyBoard(@PathVariable("userId") Long userId) {
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.lambda().eq(Board::getStatus, "active").eq(Board::getIsDeleted, 0).eq(Board::getParentId, null)
                .eq(Board::getCreatorId, userId).orderByAsc(Board::getSortOrder);
        List<Board> list = boardService.list(query);
        return ResultVo.success("查询我的板块成功", list);
    }

    //判断名字是否被占用
    @GetMapping("/isOccupied/{boardName}")
    public ResultVo isOccupied(@PathVariable("boardName") String name) {
        QueryWrapper<Board> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Board::getName, name);
        Board board = boardService.getOne(wrapper);
        if (board != null) {
            return ResultVo.success("被占用！重新填写！", true);
        }
        return ResultVo.success("没有被占用", false);
    }

    // 获取要审核的版区
    @GetMapping("/getPendingBoardList")
    public ResultVo getPendingBoardList(BoardParam param) {
        //构造分页对象
        IPage<Board> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.lambda().eq(Board::getStatus, "pending").orderByAsc(Board::getSortOrder);
        //查询
        IPage<Board> list = boardService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

    // 审核通过 修改状态
    @PutMapping("/resolved")
    public ResultVo resolved(@RequestBody Board board) {
        board.setStatus("banned"); // 默认禁用
        if (boardService.updateById(board)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    // 获取当前待审版区数目成功
    @GetMapping("/getCountOfPending")
    public ResultVo getCountOfPending() {
        QueryWrapper<Board> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Board::getIsDeleted, 0).eq(Board::getStatus, "pending");
        Long num = boardService.count(queryWrapper);
        return ResultVo.success("获取当前待审版区数目成功", num);
    }
}
