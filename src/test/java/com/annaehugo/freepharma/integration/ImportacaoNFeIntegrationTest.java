//package com.annaehugo.freepharma.integration;
//
//import com.annaehugo.freepharma.application.services.ImportacaoNFeService;
//import com.annaehugo.freepharma.domain.entity.administrativo.Farmacia;
//import com.annaehugo.freepharma.domain.entity.administrativo.Unidade;
//import com.annaehugo.freepharma.domain.entity.administrativo.UsuarioAdministrador;
//import com.annaehugo.freepharma.domain.entity.estoque.EstoqueProduto;
//import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
//import com.annaehugo.freepharma.domain.entity.estoque.ProdutoFornecedor;
//import com.annaehugo.freepharma.domain.entity.estoque.ProdutoReferencia;
//import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
//import com.annaehugo.freepharma.domain.repository.administrativo.FarmaciaRepository;
//import com.annaehugo.freepharma.domain.repository.administrativo.UnidadeRepository;
//import com.annaehugo.freepharma.domain.repository.administrativo.UsuarioAdministradorRepository;
//import com.annaehugo.freepharma.domain.repository.estoque.EstoqueProdutoRepository;
//import com.annaehugo.freepharma.domain.repository.estoque.FornecedorRepository;
//import com.annaehugo.freepharma.domain.repository.estoque.ProdutoFornecedorRepository;
//import com.annaehugo.freepharma.domain.repository.estoque.ProdutoReferenciaRepository;
//import com.annaehugo.freepharma.domain.repository.fiscal.NotaFiscalRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//public class ImportacaoNFeIntegrationTest {
//
//    @Autowired
//    private ImportacaoNFeService importacaoNFeService;
//
//    @Autowired
//    private FornecedorRepository fornecedorRepository;
//
//    @Autowired
//    private ProdutoReferenciaRepository produtoReferenciaRepository;
//
//    @Autowired
//    private ProdutoFornecedorRepository produtoFornecedorRepository;
//
//    @Autowired
//    private EstoqueProdutoRepository estoqueProdutoRepository;
//
//    @Autowired
//    private NotaFiscalRepository notaFiscalRepository;
//
//    @Autowired
//    private FarmaciaRepository farmaciaRepository;
//
//    @Autowired
//    private UnidadeRepository unidadeRepository;
//
//    @Autowired
//    private UsuarioAdministradorRepository usuarioRepository;
//
//    private Unidade unidadeTeste;
//    private UsuarioAdministrador usuarioTeste;
//
//    @BeforeEach
//    void setUp() {
//        // Criar farmacia
//        Farmacia farmacia = new Farmacia();
//        farmacia.setRazaoSocial("FreePharma Teste LTDA");
//        farmacia.setNomeFantasia("FreePharma Teste");
//        farmacia.setCnpj("12345678000199");
//        farmacia.setStatus("ATIVO");
//        farmacia.setAtivo(true);
//        farmacia = farmaciaRepository.save(farmacia);
//
//        // Criar unidade
//        unidadeTeste = new Unidade();
//        unidadeTeste.setNomeFantasia("Unidade Teste");
//        unidadeTeste.setEndereco("Rua Teste, 123");
//        unidadeTeste.setStatus("ATIVO");
//        unidadeTeste.setFarmacia(farmacia);
//        unidadeTeste.setAtivo(true);
//        unidadeTeste = unidadeRepository.save(unidadeTeste);
//
//        // Criar usuário
//        usuarioTeste = new UsuarioAdministrador();
//        usuarioTeste.setNome("Usuario Teste");
//        usuarioTeste.setCpfCnpj("12345678901");
//        usuarioTeste.setEmail("teste@freepharma.com");
//        usuarioTeste.setLogin("teste");
//        usuarioTeste.setSenha("senha123");
//        usuarioTeste.setStatus("ATIVO");
//        usuarioTeste.setUnidadesAcesso(Arrays.asList(unidadeTeste));
//        usuarioTeste.setDataCadastro(new Date());
//        usuarioTeste.setAtivo(true);
//        usuarioTeste = usuarioRepository.save(usuarioTeste);
//    }
//
//    @Test
//    void testImportacaoNFeCompraCompleta() {
//        // XML de teste simulando uma NFe de compra
//        String xmlContent = """
//            <?xml version="1.0" encoding="UTF-8"?>
//            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
//                <NFe>
//                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
//                        <ide>
//                            <nNF>1234</nNF>
//                            <serie>1</serie>
//                            <dhEmi>2024-01-15T10:00:00-03:00</dhEmi>
//                        </ide>
//                        <emit>
//                            <CNPJ>11222333000144</CNPJ>
//                            <xNome>Laboratório Distribuidor LTDA</xNome>
//                            <xFant>LabDist</xFant>
//                            <IE>123456789</IE>
//                            <enderEmit>
//                                <xLgr>Av. Industrial</xLgr>
//                                <nro>1000</nro>
//                                <xBairro>Distrito Industrial</xBairro>
//                                <xMun>São Paulo</xMun>
//                                <UF>SP</UF>
//                                <CEP>01234567</CEP>
//                            </enderEmit>
//                            <fone>1133334444</fone>
//                            <email>vendas@labdist.com</email>
//                        </emit>
//                        <dest>
//                            <CNPJ>12345678000199</CNPJ>
//                            <xNome>FreePharma Teste LTDA</xNome>
//                        </dest>
//                        <det nItem="1">
//                            <prod>
//                                <cProd>PARACETAMOL500MG</cProd>
//                                <cEAN>7891234567890</cEAN>
//                                <xProd>Paracetamol 500mg cx 20 comprimidos</xProd>
//                                <NCM>30049099</NCM>
//                                <CFOP>1102</CFOP>
//                                <uCom>CX</uCom>
//                                <qCom>50</qCom>
//                                <vUnCom>15.50</vUnCom>
//                                <vProd>775.00</vProd>
//                                <xLote>LOTE001</xLote>
//                                <dVal>2025-12-31</dVal>
//                            </prod>
//                        </det>
//                        <det nItem="2">
//                            <prod>
//                                <cProd>DIPIRONA500MG</cProd>
//                                <cEAN>7891234567891</cEAN>
//                                <xProd>Dipirona Sódica 500mg cx 10 comprimidos</xProd>
//                                <NCM>30049050</NCM>
//                                <CFOP>1102</CFOP>
//                                <uCom>CX</uCom>
//                                <qCom>30</qCom>
//                                <vUnCom>8.75</vUnCom>
//                                <vProd>262.50</vProd>
//                                <xLote>LOTE002</xLote>
//                                <dVal>2025-11-30</dVal>
//                            </prod>
//                        </det>
//                        <total>
//                            <ICMSTot>
//                                <vNF>1037.50</vNF>
//                            </ICMSTot>
//                        </total>
//                    </infNFe>
//                </NFe>
//            </nfeProc>
//            """;
//
//        MockMultipartFile file = new MockMultipartFile(
//            "file",
//            "nfe-teste.xml",
//            "text/xml",
//            xmlContent.getBytes()
//        );
//
//        // Executar importação
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(file, unidadeTeste, usuarioTeste);
//
//        // Verificar resultado
//        assertEquals("SUCESSO", resultado.get("status"));
//        assertNotNull(resultado.get("importacaoId"));
//        assertNotNull(resultado.get("notaFiscalId"));
//        assertNotNull(resultado.get("fornecedorId"));
//        assertEquals(2, resultado.get("itensProcessados"));
//
//        // Verificar se o fornecedor foi criado
//        Optional<Fornecedor> fornecedor = fornecedorRepository.findByCnpj("11222333000144");
//        assertTrue(fornecedor.isPresent());
//        assertEquals("Laboratório Distribuidor LTDA", fornecedor.get().getRazaoSocial());
//        assertEquals("LabDist", fornecedor.get().getNomeFantasia());
//        assertEquals("vendas@labdist.com", fornecedor.get().getEmail());
//        assertEquals("1133334444", fornecedor.get().getTelefone());
//
//        // Verificar se os produtos foram criados
//        List<ProdutoReferencia> produtos = produtoReferenciaRepository.findAll();
//        assertEquals(2, produtos.size());
//
//        Optional<ProdutoReferencia> paracetamol = produtoReferenciaRepository.findByEan("7891234567890");
//        assertTrue(paracetamol.isPresent());
//        assertEquals("Paracetamol 500mg cx 20 comprimidos", paracetamol.get().getNome());
//        assertEquals("30049099", paracetamol.get().getNcm());
//        assertEquals("CX", paracetamol.get().getUnidadeMedida());
//
//        Optional<ProdutoReferencia> dipirona = produtoReferenciaRepository.findByEan("7891234567891");
//        assertTrue(dipirona.isPresent());
//        assertEquals("Dipirona Sódica 500mg cx 10 comprimidos", dipirona.get().getNome());
//        assertEquals("30049050", dipirona.get().getNcm());
//
//        // Verificar se a nota fiscal foi criada
//        List<NotaFiscal> notasFiscais = notaFiscalRepository.findAll();
//        assertEquals(1, notasFiscais.size());
//
//        NotaFiscal notaFiscal = notasFiscais.get(0);
//        assertEquals("1234", notaFiscal.getNumero());
//        assertEquals("35240111222333000144550010000012341234567890", notaFiscal.getChaveAcesso());
//        assertEquals("COMPRA", notaFiscal.getTipoOperacao());
//        assertEquals("PROCESSADA", notaFiscal.getStatus());
//        assertEquals(fornecedor.get(), notaFiscal.getFornecedor());
//        assertEquals(unidadeTeste, notaFiscal.getUnidade());
//
//        // Verificar se o estoque foi atualizado corretamente
//        List<EstoqueProduto> estoques = estoqueProdutoRepository.findAll();
//        assertEquals(2, estoques.size());
//
//        // Verificar estoque do paracetamol
//        Optional<EstoqueProduto> estoqueParacetamol = estoques.stream()
//            .filter(e -> e.getProdutoReferencia().getEan().equals("7891234567890"))
//            .findFirst();
//        assertTrue(estoqueParacetamol.isPresent());
//        assertEquals(50, estoqueParacetamol.get().getQuantidadeAtual()); // Quantidade da compra
//        assertEquals("LOTE001", estoqueParacetamol.get().getLote());
//        assertNotNull(estoqueParacetamol.get().getDataVencimento());
//
//        // Verificar estoque da dipirona
//        Optional<EstoqueProduto> estoqueDipirona = estoques.stream()
//            .filter(e -> e.getProdutoReferencia().getEan().equals("7891234567891"))
//            .findFirst();
//        assertTrue(estoqueDipirona.isPresent());
//        assertEquals(30, estoqueDipirona.get().getQuantidadeAtual()); // Quantidade da compra
//        assertEquals("LOTE002", estoqueDipirona.get().getLote());
//    }
//
//    @Test
//    void testImportacaoNFeVendaComDecrementoEstoque() {
//        // Primeiro, criar estoque inicial
//        criarEstoqueInicial();
//
//        // XML de teste simulando uma NFe de venda
//        String xmlVenda = """
//            <?xml version="1.0" encoding="UTF-8"?>
//            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
//                <NFe>
//                    <infNFe Id="NFe35240112345678000199550010000056781234567890">
//                        <ide>
//                            <nNF>5678</nNF>
//                            <serie>1</serie>
//                            <dhEmi>2024-01-16T14:00:00-03:00</dhEmi>
//                        </ide>
//                        <emit>
//                            <CNPJ>12345678000199</CNPJ>
//                            <xNome>FreePharma Teste LTDA</xNome>
//                        </emit>
//                        <dest>
//                            <CPF>12345678901</CPF>
//                            <xNome>Cliente Teste</xNome>
//                        </dest>
//                        <det nItem="1">
//                            <prod>
//                                <cProd>PARACETAMOL500MG</cProd>
//                                <cEAN>7891234567890</cEAN>
//                                <xProd>Paracetamol 500mg cx 20 comprimidos</xProd>
//                                <NCM>30049099</NCM>
//                                <CFOP>5102</CFOP>
//                                <uCom>CX</uCom>
//                                <qCom>5</qCom>
//                                <vUnCom>22.00</vUnCom>
//                                <vProd>110.00</vProd>
//                                <xLote>LOTE001</xLote>
//                            </prod>
//                        </det>
//                        <total>
//                            <ICMSTot>
//                                <vNF>110.00</vNF>
//                            </ICMSTot>
//                        </total>
//                    </infNFe>
//                </NFe>
//            </nfeProc>
//            """;
//
//        MockMultipartFile file = new MockMultipartFile(
//            "file",
//            "nfe-venda-teste.xml",
//            "text/xml",
//            xmlVenda.getBytes()
//        );
//
//        // Verificar estoque antes da venda
//        List<EstoqueProduto> estoquesAntes = estoqueProdutoRepository.findAll();
//        EstoqueProduto estoqueAntes = estoquesAntes.stream()
//            .filter(e -> e.getProdutoReferencia().getEan().equals("7891234567890"))
//            .findFirst()
//            .orElseThrow();
//        assertEquals(100, estoqueAntes.getQuantidadeAtual());
//
//        // Executar importação da venda
//        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(file, unidadeTeste, usuarioTeste);
//
//        // Verificar resultado
//        assertEquals("SUCESSO", resultado.get("status"));
//
//        // Verificar se o estoque foi decrementado corretamente
//        List<EstoqueProduto> estoquesDepois = estoqueProdutoRepository.findAll();
//        EstoqueProduto estoqueDepois = estoquesDepois.stream()
//            .filter(e -> e.getProdutoReferencia().getEan().equals("7891234567890"))
//            .findFirst()
//            .orElseThrow();
//
//        assertEquals(95, estoqueDepois.getQuantidadeAtual()); // 100 - 5 = 95
//
//        // Verificar se a nota fiscal de venda foi criada
//        List<NotaFiscal> notasFiscais = notaFiscalRepository.findAll();
//        NotaFiscal notaVenda = notasFiscais.stream()
//            .filter(nf -> nf.getNumero().equals("5678"))
//            .findFirst()
//            .orElseThrow();
//
//        assertEquals("VENDA", notaVenda.getTipoOperacao());
//        assertEquals("PROCESSADA", notaVenda.getStatus());
//    }
//
//    private void criarEstoqueInicial() {
//        // Criar fornecedor
//        Fornecedor fornecedor = new Fornecedor();
//        fornecedor.setCnpj("11222333000144");
//        fornecedor.setRazaoSocial("Fornecedor Inicial");
//        fornecedor.setStatus("ATIVO");
//        fornecedor.setAtivo(true);
//        fornecedor = fornecedorRepository.save(fornecedor);
//
//        // Criar produto
//        ProdutoReferencia produto = new ProdutoReferencia();
//        produto.setCodigoInterno("PARACETAMOL500");
//        produto.setNome("Paracetamol 500mg cx 20 comprimidos");
//        produto.setEan("7891234567890");
//        produto.setNcm("30049099");
//        produto.setUnidadeMedida("CX");
//        produto.setStatus("ATIVO");
//        produto.setAtivo(true);
//        produto = produtoReferenciaRepository.save(produto);
//
//        // Criar produto fornecedor
//        ProdutoFornecedor produtoFornecedor = new ProdutoFornecedor();
//        produtoFornecedor.setProdutoReferencia(produto);
//        produtoFornecedor.setFornecedor(fornecedor);
//        produtoFornecedor.setCodigoFornecedor("PARACETAMOL500MG");
//        produtoFornecedor.setAtivo(true);
//        produtoFornecedor = produtoFornecedorRepository.save(produtoFornecedor);
//
//        // Criar estoque inicial
//        EstoqueProduto estoque = new EstoqueProduto();
//        estoque.setProdutoFornecedor(produtoFornecedor);
//        estoque.setUnidade(unidadeTeste);
//        estoque.setQuantidadeAtual(100); // Estoque inicial de 100 unidades
//        estoque.setLote("LOTE001");
//        estoque.setAtivo(true);
//        estoqueProdutoRepository.save(estoque);
//    }
//}