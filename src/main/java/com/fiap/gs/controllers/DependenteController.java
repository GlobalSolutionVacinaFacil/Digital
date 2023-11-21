package com.fiap.gs.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.gs.exception.RestNotAuthorized;
import com.fiap.gs.exception.RestNotFoundException;
import com.fiap.gs.exception.RestNotValidBodyException;
import com.fiap.gs.models.Aviso;
import com.fiap.gs.models.Dependente;
import com.fiap.gs.models.Usuario;
import com.fiap.gs.repository.AvisoRepository;
import com.fiap.gs.repository.DependentesRepository;
import com.fiap.gs.repository.UsuarioRepository;
import com.fiap.gs.service.AvisoService;
import com.fiap.gs.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dependente")
public class DependenteController {

	@Autowired
	DependentesRepository repository;

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	AvisoRepository avisoRepository;

	@Autowired
	AuthenticationManager manager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	TokenService tokenService;

	@Autowired
	AvisoService avisoService;
	
	@PostMapping("/cadastrar")
	public ResponseEntity<Dependente> registrar(@RequestBody @Valid Dependente dependente) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RestNotAuthorized(
				"Somente o próprio usuário pode criar um dependente vinculado a própria conta."));

		dependente.setUsuario(usuario);
		repository.save(dependente);
		
		avisoService.geradorDeAvisos(dependente);

		return ResponseEntity.status(HttpStatus.CREATED).body(dependente);
	}
	
	@PutMapping
	public ResponseEntity<Dependente> atualizar(@RequestBody Dependente dependente) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RestNotAuthorized(
				"Somente o próprio usuário pode criar um dependente vinculado a própria conta."));

		
		if(dependente.getId() == null) throw new RestNotValidBodyException("É necessário informar o dependente pelo ID");
		
		Dependente dependenteSelecionado = repository.findById(dependente.getId());
		if(dependenteSelecionado == null) throw new RestNotFoundException("Dependente não encontrado");
	
		
		if(dependente.getNome() == null) dependente.setNome(dependenteSelecionado.getNome());
		else dependente.setNome(dependente.getNome());
		
		if(dependente.getDataNascimento() == null) dependente.setDataNascimento(dependenteSelecionado.getDataNascimento());
		else dependente.setDataNascimento(dependente.getDataNascimento());
		
		dependente.setId(dependenteSelecionado.getId());
		dependente.setUsuario(usuario);
		
		repository.save(dependente);

		return ResponseEntity.status(HttpStatus.OK).body(dependente);
	}
	
    @GetMapping
    public ResponseEntity<Page<Dependente>> index(@PageableDefault(size = 5) Pageable pageable){
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RestNotAuthorized(
				"Somente o próprio usuário pode buscar dependentes vinculado a própria conta."));
		
        Page<Dependente> dependentes = repository.findByUsuarioIdOrderByIdDesc(usuario.getId(), pageable);

        return ResponseEntity.status(HttpStatus.CREATED).body(dependentes);
    }
    
	@DeleteMapping("{id}")
	public ResponseEntity<Usuario> delete(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new RestNotAuthorized("Somente o próprio usuário pode deletar seus dependentes."));

		Dependente dependenteSelecionado = repository.findById(id).orElseThrow(() -> new RestNotFoundException("Dependente não encontrado"));
		
		if(dependenteSelecionado.getUsuario().getId() == usuario.getId()) {
			List<Aviso> avisos = avisoRepository.findAllByDependente(dependenteSelecionado);
			avisos.forEach((aviso) ->{
				avisoRepository.delete(aviso);
			});
			repository.delete(dependenteSelecionado);
		}
		else throw new RestNotAuthorized("Somente o próprio usuário pode deletar seus dependentes.");

		return ResponseEntity.noContent().build();
	}

}
