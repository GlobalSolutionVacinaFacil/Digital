package com.fiap.gs.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.gs.exception.RestNotAuthorized;
import com.fiap.gs.exception.RestNotFoundException;
import com.fiap.gs.models.Aviso;
import com.fiap.gs.models.Dependente;
import com.fiap.gs.models.Usuario;
import com.fiap.gs.models.Vacina;
import com.fiap.gs.repository.AvisoRepository;
import com.fiap.gs.repository.DependentesRepository;
import com.fiap.gs.repository.UsuarioRepository;
import com.fiap.gs.repository.VacinaRepository;

@RestController
@RequestMapping("/api/aviso")
public class AvisoController {

	@Autowired
	AvisoRepository repository;
	
	@Autowired
	DependentesRepository dependenteRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	

	@GetMapping("{id}")
	public ResponseEntity<List<Aviso>> list(@PathVariable Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new RestNotAuthorized("Somente o próprio usuário pode deletar listar avisos de dependentes"));

		
		Dependente dependente = dependenteRepository.findById(id).orElseThrow(() -> new RestNotFoundException("Dependente não encontrado"));
		
		if(dependente.getUsuario().getId() != usuario.getId()) {
			throw new RestNotAuthorized("Somente o reponsável do dependente pode realizar este método");
		}
		
		List<Aviso> avisos = repository.findAll();
		List<Aviso> resultado = new ArrayList<>();
		
		avisos.forEach((aviso) -> {
			if(aviso.getDependente().getId() == dependente.getId()) {
				resultado.add(aviso);
			}
		});
		
		
		
		return ResponseEntity.ok(resultado);
	}
	
	

}
