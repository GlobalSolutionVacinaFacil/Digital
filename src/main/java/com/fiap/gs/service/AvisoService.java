package com.fiap.gs.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiap.gs.models.Aviso;
import com.fiap.gs.models.Dependente;
import com.fiap.gs.models.TipoVacina;
import com.fiap.gs.models.Vacina;
import com.fiap.gs.models.VacinaParaTomar;
import com.fiap.gs.repository.AvisoRepository;
import com.fiap.gs.repository.VacinaRepository;

@Service
public class AvisoService {

	@Autowired
	VacinaRepository vacinaRepository;

	@Autowired
	AvisoRepository avisoRepository;

	public void geradorDeAvisos(Dependente dependente) {

		List<Vacina> vacinas = vacinaRepository.findAll();
		List<VacinaParaTomar> vacinasParaTomar = new ArrayList<>();

		Integer idadeEmMeses = Period
				.between(dependente.getDataNascimento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
						new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
				.getMonths();

        LocalDate dataDeNascimento = LocalDate.now().minusMonths(idadeEmMeses);
		
		for (Vacina vacina : vacinas) {
			if (vacina.getTipo() == TipoVacina.UNIQUE && vacina.getPrimeiroMes() > idadeEmMeses) {
				VacinaParaTomar vacinaParaTomar = new VacinaParaTomar();
				 LocalDate dataDeAplicacao = dataDeNascimento.plusMonths(vacina.getPrimeiroMes());
				 
				vacinaParaTomar.setVacina(vacina);
				vacinaParaTomar.setAplicacao(Date.from(dataDeAplicacao.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				vacinasParaTomar.add(vacinaParaTomar);
			}
			if (vacina.getTipo() == TipoVacina.PAIR) {
				if (vacina.getPrimeiroMes() > idadeEmMeses) {
					VacinaParaTomar vacinaParaTomar = new VacinaParaTomar();
					 LocalDate dataDeAplicacao = dataDeNascimento.plusMonths(vacina.getPrimeiroMes());
					 
					vacinaParaTomar.setVacina(vacina);
					vacinaParaTomar.setAplicacao(Date.from(dataDeAplicacao.atStartOfDay(ZoneId.systemDefault()).toInstant()));
					vacinasParaTomar.add(vacinaParaTomar);
				}
				if (vacina.getPrimeiroMes() + vacina.getIntervalo() > idadeEmMeses) {
					VacinaParaTomar vacinaParaTomar = new VacinaParaTomar();
					 LocalDate dataDeAplicacao = dataDeNascimento.plusMonths(vacina.getPrimeiroMes() + vacina.getIntervalo());
					 
					vacinaParaTomar.setVacina(vacina);
					vacinaParaTomar.setAplicacao(Date.from(dataDeAplicacao.atStartOfDay(ZoneId.systemDefault()).toInstant()));
					vacinasParaTomar.add(vacinaParaTomar);
				}
			}
			if (vacina.getTipo() == TipoVacina.TRIPLE) {
				if (vacina.getPrimeiroMes() > idadeEmMeses) {
					VacinaParaTomar vacinaParaTomar = new VacinaParaTomar();
					 LocalDate dataDeAplicacao = dataDeNascimento.plusMonths(vacina.getPrimeiroMes());
					 
					vacinaParaTomar.setVacina(vacina);
					vacinaParaTomar.setAplicacao(Date.from(dataDeAplicacao.atStartOfDay(ZoneId.systemDefault()).toInstant()));
					vacinasParaTomar.add(vacinaParaTomar);
				}
				if (vacina.getPrimeiroMes() + vacina.getIntervalo() > idadeEmMeses) {
					VacinaParaTomar vacinaParaTomar = new VacinaParaTomar();
					 LocalDate dataDeAplicacao = dataDeNascimento.plusMonths(vacina.getPrimeiroMes() + vacina.getIntervalo());
					 
					vacinaParaTomar.setVacina(vacina);
					vacinaParaTomar.setAplicacao(Date.from(dataDeAplicacao.atStartOfDay(ZoneId.systemDefault()).toInstant()));
					vacinasParaTomar.add(vacinaParaTomar);
				}
				if (vacina.getPrimeiroMes() + (2 * vacina.getIntervalo()) > idadeEmMeses) {
					VacinaParaTomar vacinaParaTomar = new VacinaParaTomar();
					 LocalDate dataDeAplicacao = dataDeNascimento.plusMonths(vacina.getPrimeiroMes() + (2 * vacina.getIntervalo()));
					 
					vacinaParaTomar.setVacina(vacina);
					vacinaParaTomar.setAplicacao(Date.from(dataDeAplicacao.atStartOfDay(ZoneId.systemDefault()).toInstant()));
					vacinasParaTomar.add(vacinaParaTomar);
				}
			}
		};
		
		
		for (VacinaParaTomar vacinaParaTomar : vacinasParaTomar) {
			Aviso aviso = new Aviso();
			aviso.setDependente(dependente);
			aviso.setUsuario(dependente.getUsuario());
			aviso.setVacina(vacinaParaTomar.getVacina());
			aviso.setData(vacinaParaTomar.getAplicacao());
			
			avisoRepository.save(aviso);
		};

	}

}
