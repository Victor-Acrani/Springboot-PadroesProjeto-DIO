package br.com.acrani.labpadroesprojetospring.services;

import br.com.acrani.labpadroesprojetospring.dto.ClienteDtoRequest;
import br.com.acrani.labpadroesprojetospring.models.Cliente;
import br.com.acrani.labpadroesprojetospring.models.Endereco;
import br.com.acrani.labpadroesprojetospring.repositories.ClienteRepository;
import br.com.acrani.labpadroesprojetospring.repositories.EnderecoRepository;
import br.com.acrani.labpadroesprojetospring.services.exceptions.DuplicatedDataException;
import br.com.acrani.labpadroesprojetospring.services.exceptions.ResourceNotFoundException;
import br.com.acrani.labpadroesprojetospring.services.interfaces.IClienteService;
import br.com.acrani.labpadroesprojetospring.services.interfaces.IViaCepService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring via construtor. Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 *
 * @author victor
 */

@Service
public class ClienteService implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final IViaCepService iViaCepService;

    public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository, IViaCepService iViaCepService) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.iViaCepService = iViaCepService;
    }

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public Cliente save(ClienteDtoRequest clienteDtoRequest) {
        //verifica se o cliente já está cadastrado
        Optional<Cliente> byNomeAndCep = clienteRepository.findByNome(clienteDtoRequest.getNome());
        if (byNomeAndCep.isPresent()){
            throw new DuplicatedDataException("Costumer already registered!");
        }

        //cadastra cliente
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDtoRequest.getNome());
        String cep = clienteDtoRequest.getCep();

        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco novoEndereco = iViaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });

        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
        return cliente;
    }

    @Override
    public Cliente update(Long id, ClienteDtoRequest clienteDtoRequest) {
        Cliente cliente = findById(id);
        cliente.setNome(clienteDtoRequest.getNome());
        String cep = clienteDtoRequest.getCep();

        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco novoEndereco = iViaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });

        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
        return cliente;
    }

    @Override
    public void delete(Long id) {
        try{
            clienteRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException(id);
        }

    }

}
