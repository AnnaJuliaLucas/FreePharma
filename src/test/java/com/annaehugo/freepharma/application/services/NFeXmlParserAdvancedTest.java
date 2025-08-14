package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("NFeXmlParser - Cenários Avançados")
public class NFeXmlParserAdvancedTest {
    
    private NFeXmlParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new NFeXmlParser();
    }
    
    @Test
    @DisplayName("Deve parsear NFe-e (NFC-e) corretamente")
    void testParseNFCe() {
        String xmlNFCe = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240112345678000199650010000012341234567890">
                        <ide>
                            <mod>65</mod>
                            <nNF>1234</nNF>
                            <serie>1</serie>
                            <dhEmi>2024-01-15T10:00:00-03:00</dhEmi>
                        </ide>
                        <emit>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>FreePharma Matriz</xNome>
                        </emit>
                        <dest>
                            <CPF>12345678901</CPF>
                            <xNome>Cliente Final</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>PRODUTO001</cProd>
                                <xProd>Produto Venda Balcão</xProd>
                                <CFOP>5102</CFOP>
                                <qCom>1</qCom>
                                <vUnCom>10.50</vUnCom>
                                <vProd>10.50</vProd>
                            </prod>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>10.50</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlNFCe.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        assertEquals("VENDA", result.getTipoOperacao());
        assertEquals("12345678901", result.getDestinatario().getCnpjCpf());
        assertEquals(new BigDecimal("10.50"), result.getValorTotal());
    }
    
    @DisplayName("Deve identificar diferentes CFOPs de compra")
    @ParameterizedTest
    @ValueSource(strings = {"1102", "1151", "1202", "1203", "2102", "2151"})
    void testIdentificaCFOPsCompra(String cfop) {
        String xmlTemplate = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>PROD001</cProd>
                                <xProd>Produto</xProd>
                                <CFOP>%s</CFOP>
                                <qCom>1</qCom>
                                <vUnCom>10.00</vUnCom>
                                <vProd>10.00</vProd>
                            </prod>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>10.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """.formatted(cfop);
        
        byte[] xmlBytes = xmlTemplate.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        assertEquals("COMPRA", result.getTipoOperacao(), 
            "CFOP " + cfop + " deveria ser identificado como COMPRA");
    }
    
    @DisplayName("Deve identificar diferentes CFOPs de venda")
    @ParameterizedTest
    @ValueSource(strings = {"5102", "5151", "5202", "5203", "6102", "6108"})
    void testIdentificaCFOPsVenda(String cfop) {
        String xmlTemplate = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </emit>
                        <dest>
                            <CPF>12345678901</CPF>
                            <xNome>Cliente</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>PROD001</cProd>
                                <xProd>Produto</xProd>
                                <CFOP>%s</CFOP>
                                <qCom>1</qCom>
                                <vUnCom>10.00</vUnCom>
                                <vProd>10.00</vProd>
                            </prod>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>10.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """.formatted(cfop);
        
        byte[] xmlBytes = xmlTemplate.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        assertEquals("VENDA", result.getTipoOperacao(), 
            "CFOP " + cfop + " deveria ser identificado como VENDA");
    }
    
    @Test
    @DisplayName("Deve parsear medicamentos controlados com informações especiais")
    void testParseMedicamentoControlado() {
        String xmlControlado = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Laboratório</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>CLONAZEPAM2MG</cProd>
                                <cEAN>7891234567892</cEAN>
                                <xProd>Clonazepam 2mg cx 30 comprimidos - MEDICAMENTO CONTROLADO</xProd>
                                <NCM>30049019</NCM>
                                <CFOP>1102</CFOP>
                                <uCom>CX</uCom>
                                <qCom>10</qCom>
                                <vUnCom>45.80</vUnCom>
                                <vProd>458.00</vProd>
                                <xLote>LOTE456</xLote>
                                <dVal>2025-06-30</dVal>
                            </prod>
                            <med>
                                <nLote>LOTE456</nLote>
                                <qLote>10</qLote>
                                <dFab>2023-07-01</dFab>
                                <dVal>2025-06-30</dVal>
                                <vPMC>65.90</vPMC>
                            </med>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>458.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlControlado.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        NFeXmlData.ItemNFeDados item = result.getItens().get(0);
        
        assertEquals("CLONAZEPAM2MG", item.getCodigoProduto());
        assertTrue(item.getNomeProduto().contains("MEDICAMENTO CONTROLADO"));
        assertEquals("30049019", item.getNcm()); // NCM específico de medicamentos
        assertEquals("LOTE456", item.getLote());
        assertNotNull(item.getDataVencimento());
    }
    
    @Test
    @DisplayName("Deve parsear NFe com múltiplos lotes do mesmo produto")
    void testParseMultiplosLotes() {
        String xmlMultiplosLotes = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>PARACETAMOL500</cProd>
                                <cEAN>7891234567890</cEAN>
                                <xProd>Paracetamol 500mg cx 20 comp</xProd>
                                <CFOP>1102</CFOP>
                                <qCom>25</qCom>
                                <vUnCom>15.50</vUnCom>
                                <vProd>387.50</vProd>
                                <xLote>LOTE001</xLote>
                                <dVal>2025-12-31</dVal>
                            </prod>
                        </det>
                        <det nItem="2">
                            <prod>
                                <cProd>PARACETAMOL500</cProd>
                                <cEAN>7891234567890</cEAN>
                                <xProd>Paracetamol 500mg cx 20 comp</xProd>
                                <CFOP>1102</CFOP>
                                <qCom>25</qCom>
                                <vUnCom>15.50</vUnCom>
                                <vProd>387.50</vProd>
                                <xLote>LOTE002</xLote>
                                <dVal>2025-11-30</dVal>
                            </prod>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>775.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlMultiplosLotes.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        assertEquals(2, result.getItens().size());
        
        // Mesmo produto, lotes diferentes
        NFeXmlData.ItemNFeDados item1 = result.getItens().get(0);
        NFeXmlData.ItemNFeDados item2 = result.getItens().get(1);
        
        assertEquals(item1.getCodigoProduto(), item2.getCodigoProduto());
        assertEquals(item1.getEan(), item2.getEan());
        assertNotEquals(item1.getLote(), item2.getLote());
        
        assertEquals("LOTE001", item1.getLote());
        assertEquals("LOTE002", item2.getLote());
    }
    
    @Test
    @DisplayName("Deve lidar com formatos de data diferentes")
    void testFormatosDataDiferentes() {
        String xmlDataSemTimezone = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                            <dhEmi>2024-01-15T10:00:00</dhEmi>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </dest>
                        <total>
                            <ICMSTot>
                                <vNF>100.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlDataSemTimezone.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        assertNotNull(result.getDataEmissao());
    }
    
    @Test
    @DisplayName("Deve validar estrutura XML mínima obrigatória")
    void testValidacaoEstruturaMinima() {
        String xmlSemTotal = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </dest>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlSemTotal.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        // Deve conseguir parsear mesmo sem o total (valor default)
        assertEquals(BigDecimal.ZERO, result.getValorTotal());
    }
    
    @Test
    @DisplayName("Deve extrair informações de tributos quando disponíveis")
    void testExtracaoTributos() {
        String xmlComTributos = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>PRODUTO001</cProd>
                                <xProd>Produto Teste</xProd>
                                <CFOP>1102</CFOP>
                                <qCom>1</qCom>
                                <vUnCom>100.00</vUnCom>
                                <vProd>100.00</vProd>
                            </prod>
                            <imposto>
                                <ICMS>
                                    <ICMS00>
                                        <orig>0</orig>
                                        <CST>00</CST>
                                        <modBC>0</modBC>
                                        <vBC>100.00</vBC>
                                        <pICMS>18.00</pICMS>
                                        <vICMS>18.00</vICMS>
                                    </ICMS00>
                                </ICMS>
                                <PIS>
                                    <PISAliq>
                                        <CST>01</CST>
                                        <vBC>100.00</vBC>
                                        <pPIS>1.65</pPIS>
                                        <vPIS>1.65</vPIS>
                                    </PISAliq>
                                </PIS>
                                <COFINS>
                                    <COFINSAliq>
                                        <CST>01</CST>
                                        <vBC>100.00</vBC>
                                        <pCOFINS>7.60</pCOFINS>
                                        <vCOFINS>7.60</vCOFINS>
                                    </COFINSAliq>
                                </COFINS>
                            </imposto>
                        </det>
                        <total>
                            <ICMSTot>
                                <vBC>100.00</vBC>
                                <vICMS>18.00</vICMS>
                                <vICMSDeson>0.00</vICMSDeson>
                                <vBCST>0.00</vBCST>
                                <vST>0.00</vST>
                                <vProd>100.00</vProd>
                                <vFrete>0.00</vFrete>
                                <vSeg>0.00</vSeg>
                                <vDesc>0.00</vDesc>
                                <vII>0.00</vII>
                                <vIPI>0.00</vIPI>
                                <vPIS>1.65</vPIS>
                                <vCOFINS>7.60</vCOFINS>
                                <vOutro>0.00</vOutro>
                                <vNF>100.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlComTributos.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        // Verificar se os dados principais são extraídos corretamente
        assertEquals(new BigDecimal("100.00"), result.getValorTotal());
        assertEquals(1, result.getItens().size());
        
        NFeXmlData.ItemNFeDados item = result.getItens().get(0);
        assertEquals(new BigDecimal("100.00"), item.getValorTotal());
    }
    
    @Test
    @DisplayName("Deve detectar erros em XMLs corrompidos")
    void testXMLCorrompido() {
        String xmlCorrompido = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        <!-- Tag não fechada corretamente
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Farmacia</xNome>
                        </dest>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlCorrompido.getBytes();
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            parser.parseNFeXml(xmlBytes);
        });
        
        assertTrue(exception.getMessage().contains("Erro ao fazer parse do XML"));
    }
}