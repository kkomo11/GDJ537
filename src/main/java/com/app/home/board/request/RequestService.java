package com.app.home.board.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.home.board.BoardDAO;
import com.app.home.board.BoardVO;
import com.app.home.file.FileDAO;
import com.app.home.file.FileVO;
import com.app.home.util.FileManager;
import com.app.home.util.Pager;

import lombok.extern.slf4j.Slf4j;

@Service
public class RequestService {
	
	@Autowired
	private BoardDAO boardDAO;
	@Value("${app.file.base.board}")
	private String path;
	@Autowired
	private FileManager fileManager;
	@Autowired
	private FileDAO fileDAO;
	
	public boolean checkValid(BoardVO boardVO) {
		boolean chkId = false;
		boolean chkTitle = false;
		boolean chkContents = false;
		try {
			chkId = boardVO.getId()!=null;
			chkTitle = boardVO.getTitle()!=null;
			chkContents = boardVO.getContents()!=null;			
		}catch(Exception exception) {
			return false;
		}

				
		return chkId&chkTitle&chkContents;
	}
	
	public int setRequestDelete(BoardVO boardVO) throws Exception{
		int result = boardDAO.setDelete(boardVO);
		
		return result;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public int setRequest(BoardVO boardVO) throws Exception{
		//sort에 2 (Request) 세팅
		boardVO.setSort(2);
		
		int result = boardDAO.setBoard(boardVO);
		
		if(result == 1 && boardVO.getMultipartFiles() !=null) {
			for(MultipartFile file : boardVO.getMultipartFiles()) {
				if(file.getOriginalFilename()!="") {
					FileVO fileVO = new FileVO();
					String fileName = fileManager.saveFile(file, path);
					fileVO.setFileName(fileName);
					fileVO.setOriName(file.getOriginalFilename());
					fileVO.setNum(boardVO.getNum());
					
					int result2 =fileDAO.setFile(fileVO);
				}
			}
		}

		
		return result;
	}
	
	public BoardVO getDetail(BoardVO boardVO) throws Exception{
		boardVO = boardDAO.getDetail(boardVO);
		
		return boardVO;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public int setUpdate(BoardVO boardVO) throws Exception{
		int result = boardDAO.setUpdate(boardVO);
		
		if(result == 1 && boardVO.getMultipartFiles() !=null) {
			for(MultipartFile file : boardVO.getMultipartFiles()) {
				if(file.getOriginalFilename()!="") {
					FileVO fileVO = new FileVO();
					String fileName = fileManager.saveFile(file, path);
					fileVO.setFileName(fileName);
					fileVO.setOriName(file.getOriginalFilename());
					fileVO.setNum(boardVO.getNum());
					
					int result2 =fileDAO.setFile(fileVO);
				}
			}
		}
		
		return result;
	}

	public List<BoardVO> getRequestList(Pager pager) throws Exception {
		
		Long totalCount = boardDAO.getTotalCount(pager);
		pager.getRowNum();
		pager.getNum(totalCount);
		
		return boardDAO.getList(pager);
	}
	
	public int setHit(BoardVO boardVO) throws Exception {
		return boardDAO.setHit(boardVO);
	}
}
