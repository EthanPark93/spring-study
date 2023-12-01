package ethan.springbootboard.service;

import ethan.springbootboard.dto.BoardDTO;
import ethan.springbootboard.entity.BoardEntity;
import ethan.springbootboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// DTO -> Entity (Entiry Class)
// Entity -> DTO (DTO Class)

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void save(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();

//        for (BoardEntity boardEntity : boardEntityList) {
//            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
//        }

        boardEntityList.stream().forEach(one -> boardDTOList.add(BoardDTO.toBoardDTO(one)));

        return boardDTOList;
    }
}
