package com.iaglourenco;

import com.iaglourenco.exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

class Interface  {

    private static Interface instance;

    private Sistema sistema = Sistema.getInstance();
    private JFrame saidaVeiculos = new JFrame();
    private JFrame entradaVeiculos = new JFrame();
    private JFrame setupEstacionamento = new JFrame();
    private JFrame contabilidade = new JFrame();
    private JFrame status = new JFrame();
    private JFrame pagamento = new JFrame();
    private Dimension frameDimension = new Dimension(1000,700);
    private Dimension popupDimension = new Dimension(300,260);

    private JButton buttonEntrada=new JButton("Registrar entrada");//registrar entrada
    private JButton buttonSaida=new JButton("Registrar saida");//registrar saida
    private JButton buttonContabilidade = new JButton("Contabilidade");
    private JButton buttonSetup = new JButton("Configurar preÃ§os");
    private JButton buttonExit=new JButton("Sair");//sair do programa
    private JButton buttonTerreo =new JButton("Terreo");//visualizar piso terreo
    private JButton buttonPriPiso =new JButton("1° Piso");//visualizar primeiro piso
    private JTextArea infoCarro = new JTextArea();
    private JTextArea infoMoto = new JTextArea();
    private JTextArea infoCaminhonete = new JTextArea();
    private JPanel panel1Status = new JPanel(new FlowLayout());
    private JPanel panel2Status = new JPanel(new FlowLayout());
    
    private JPanel carroStatus = new JPanel(new FlowLayout());
    private JPanel caminhoneteStatus = new JPanel(new FlowLayout());
    private JPanel motoStatus = new JPanel(new FlowLayout());
    
    private JButton buttonEstacCarro[] = new JButton[160];
    private JButton buttonEstacCaminhonete[] = new JButton[20];
    private JButton buttonEstacMoto[] = new JButton[20];
    
    private JPanel panel1Setup = new JPanel(new GridLayout(4,0,10,10));//TODO encontrar um layout melhor
    private JTextField precoCaminhonete = new JTextField();
    private JTextField precoCarro = new JTextField();
    private JTextField precoMotocicleta = new JTextField();
    private JButton buttonOKSetup = new JButton("OK");
    private JButton buttonClearSetup = new JButton("Limpar");

    private final String[] names = {"Selecione...","Carro","Caminhonete","Motocicleta"};
    private JComboBox<String> categoria = new JComboBox<>(names); //Para painel Entrada e Saida

    private JPanel panelSaida = new JPanel(new GridLayout(7,0,10,10));
    private JTextField placaSaida = new JTextField();
    private JTextField horaSaida = new JTextField();
    private JButton buttonOKSaida = new JButton("OK");
    private JButton buttonBackSaida = new JButton("Voltar");


    private JPanel panelEntrada = new JPanel(new GridLayout(7,0,10,10));
    private JTextField placaEntrada = new JTextField();
    private JTextField horaEntrada = new JTextField();
    private JButton buttonOKEntrada = new JButton("OK");
    private JButton buttonBackEntrada = new JButton("Voltar");


    private JPanel panelContabilidade = new JPanel(new FlowLayout());
    private JTextArea contabArea = new JTextArea();
    private JButton buttonOKContabilidade = new JButton("OK");

    private JPanel panelPagamento = new JPanel();
    private JTextArea infoPlaca = new JTextArea();
    private JTextArea infoPreco = new JTextArea();
    private JTextArea infoTipo = new JTextArea();
    private JButton buttonOKPagamento = new JButton("OK");



    private  Interface(){
        initialize();
        //initSetup();
        setupEstacionamento.setVisible(true);
        sistema.setup();
        try {
            sistema.updateVagasFile();
        } catch (WriteFileException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERRO AO ESCREVER ARQUIVO",JOptionPane.ERROR_MESSAGE);
        }
        initEntrada();
        initSaida();
        initPagamento();
        initContabilidade();
        addAllHandlers();
    }

    private void addAllHandlers(){

        buttonEntrada.addActionListener(new StatusHandler());
        buttonSaida.addActionListener(new StatusHandler());
        buttonContabilidade.addActionListener(new StatusHandler());
        buttonSetup.addActionListener(new StatusHandler());
        buttonExit.addActionListener(new StatusHandler());
        buttonTerreo.addActionListener(new StatusHandler());
        buttonPriPiso.addActionListener(new StatusHandler());
        
        status.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        status.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(JOptionPane.showConfirmDialog
                        (null,"Tem certeza?",
                                "Sair",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                {
                    System.exit(0);

                }

            }
        });

        buttonOKSetup.addActionListener(new SetupHandler());
        buttonClearSetup.addActionListener(new SetupHandler());
        
        setupEstacionamento.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setupEstacionamento.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(precoCaminhonete.getText().isEmpty() || precoCarro.getText().isEmpty() || precoMotocicleta.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Preencha todos os campos!","ERRO",JOptionPane.ERROR_MESSAGE);
                }else{

                    sistema.setPrecoCaminhonete(Double.parseDouble(precoCaminhonete.getText()));
                    sistema.setPrecoCarro(Double.parseDouble(precoCarro.getText()));
                    sistema.setPrecoMoto(Double.parseDouble(precoMotocicleta.getText()));
                    JOptionPane.showMessageDialog(null,"Os preÃ§os digitados foram salvos!","INFO",JOptionPane.INFORMATION_MESSAGE);
                    setupEstacionamento.dispose();
                }
            }
        });
        setupEstacionamento.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                setupEstacionamento.requestFocus();
            }
        });

        categoria.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                System.out.println(e.getItem().toString());
            }
        });


        buttonOKEntrada.addActionListener(new EntradaHandler());
        buttonBackEntrada.addActionListener(new EntradaHandler());
        
        buttonOKSaida.addActionListener(new SaidaHandler());
        buttonBackSaida.addActionListener(new SaidaHandler());
        
        buttonOKContabilidade.addActionListener(new ContabileHandler());

    }

    private void initialize(){
        //LAYOUT PRINCIPAL
        status.setLayout(new BorderLayout());
        status.setSize(frameDimension);
        status.setResizable(false);
        status.setLocationRelativeTo(null);
        status.setTitle("Paradigmas-B [Controle de Estacionamento]");

      //STATUS TERREO
        panel2Status.add(buttonTerreo);
        
        //STATUS 1°PISO
        panel2Status.add(buttonPriPiso);
        
      //Visualização Geral
        panel2Status.add(new JLabel("                                              Carros "));
        panel2Status.add(infoCarro);
        infoCarro.setEditable(false);
        infoCarro.setText(" 55 / 160 ");

        panel2Status.add(new JLabel("      Caminhonetes "));
        panel2Status.add(infoCaminhonete);
        infoCaminhonete.setEditable(false);
        infoCaminhonete.setText(" 10 / 20 ");

        panel2Status.add(new JLabel("      Motocicletas "));
        panel2Status.add(infoMoto);
        infoMoto.setEditable(false);
        infoMoto.setText(" 20 / 20");
        
        panel1Status.add(buttonEntrada);
        panel1Status.add(buttonSaida);
        panel1Status.add(buttonContabilidade);
        panel1Status.add(buttonSetup);
        panel1Status.add(buttonExit);
        
        //TODO inicializar a visualizacao de vagas disponiveis
        //buttonEstacCarro.setOpaque(false);
        //buttonEstacCarro.setContentAreaFilled(false);
        //buttonEstacCarro.setBorderPainted(false);
        
        carroStatus.setPreferredSize(new Dimension(250, 300));
        for(int i = 0; i < 160; i++) {
        	buttonEstacCarro[i] = new JButton(String.valueOf(i));
        	buttonEstacCarro[i].setSize(4,6);
            buttonEstacCarro[i].setBackground(Color.green);
            buttonEstacCarro[i].setText("C"+i);
            carroStatus.add(buttonEstacCarro[i]);
        }
        
        //buttonEstacCaminhonete.setOpaque(false);
        //buttonEstacCaminhonete.setContentAreaFilled(false);
        //buttonEstacCaminhonete.setBorderPainted(false);
        //buttonEstacCaminhonete.setPreferredSize(new Dimension(150,100));
        caminhoneteStatus.setPreferredSize(new Dimension(150, 100));
        for(int i = 0; i < 20; i++) {
        	buttonEstacCaminhonete[i] = new JButton(String.valueOf(i));
        	buttonEstacCaminhonete[i].setSize(4,6);
            buttonEstacCaminhonete[i].setBackground(Color.blue);
            buttonEstacCaminhonete[i].setText("Ca"+i);
            caminhoneteStatus.add(buttonEstacCaminhonete[i]);
        }
        
        //buttonEstacMoto.setOpaque(false);
        //buttonEstacMoto.setContentAreaFilled(false);
        //buttonEstacMoto.setBorderPainted(false);
        //buttonEstacMoto.setPreferredSize(new Dimension(150,100));
        motoStatus.setPreferredSize(new Dimension(150, 100));
        for(int i = 0; i < 20; i++) {
        	buttonEstacMoto[i] = new JButton(String.valueOf(i));
        	buttonEstacMoto[i].setSize(4,6);
            buttonEstacMoto[i].setBackground(Color.red);
            buttonEstacMoto[i].setText("M"+i);
            motoStatus.add(buttonEstacMoto[i]);
        }
        

        status.add(panel1Status,BorderLayout.SOUTH);
        status.add(panel2Status,BorderLayout.NORTH);
        status.add(carroStatus,BorderLayout.CENTER);
        status.add(caminhoneteStatus,BorderLayout.LINE_START);
        status.add(motoStatus,BorderLayout.LINE_END);
        status.setVisible(true);
    }

    private void initSetup(){
        //LAYOUT DE CONFIGURAR PREÃ‡OS
        setupEstacionamento.setLayout(new BorderLayout());
        setupEstacionamento.setSize(popupDimension);
        setupEstacionamento.setResizable(false);
        setupEstacionamento.setLocationRelativeTo(null);
        setupEstacionamento.setTitle("Configurar PreÃ§os");

        panel1Setup.add(new JLabel("Carros / R$:"));
        panel1Setup.add(precoCarro);

        panel1Setup.add(new JLabel("Caminhonetes / R$:"));
        panel1Setup.add(precoCaminhonete);

        panel1Setup.add(new JLabel("Motocicletas / R$:"));
        panel1Setup.add(precoMotocicleta);

        precoCarro.setText(Double.toString(sistema.getPrecoCarro()));
        precoCaminhonete.setText(Double.toString(sistema.getPrecoCaminhonete()));
        precoMotocicleta.setText(Double.toString(sistema.getPrecoMoto()));

        panel1Setup.add(buttonOKSetup);
        panel1Setup.add(buttonClearSetup);

        setupEstacionamento.add(panel1Setup,BorderLayout.CENTER);

    }

    private void initSaida(){
        //LAYOUT REGISTRO DE SAIDA
        saidaVeiculos.setLayout(new BorderLayout());
        saidaVeiculos.setSize(popupDimension);
        saidaVeiculos.setResizable(false);
        saidaVeiculos.setLocationRelativeTo(null);
        saidaVeiculos.setTitle("Registrar saida");

        panelSaida.add(new JLabel("Placa do Veiculo"));
        panelSaida.add(placaSaida);
        panelSaida.add(new JLabel("Horario de Saida"));
        panelSaida.add(horaSaida);
        categoria = new JComboBox<>(names);
        categoria.setMaximumRowCount(3);
        panelSaida.add(categoria);
        panelSaida.add(buttonOKSaida);
        panelSaida.add(buttonBackSaida);

        saidaVeiculos.add(panelSaida);

    }

    private void initEntrada(){
        //LAYOUT REGISTRO DE ENTRADA
        entradaVeiculos.setLayout(new BorderLayout());
        entradaVeiculos.setSize(popupDimension);
        entradaVeiculos.setResizable(false);
        entradaVeiculos.setLocationRelativeTo(null);
        entradaVeiculos.setTitle("Registrar entrada");

        panelEntrada.add(new JLabel("Placa do Veiculo"));
        panelEntrada.add(placaEntrada);
        panelEntrada.add(new JLabel("Horario de Entrada"));
        panelEntrada.add(horaEntrada);
        categoria.setMaximumRowCount(names.length);
        panelEntrada.add(categoria);
        panelEntrada.add(buttonOKEntrada);
        panelEntrada.add(buttonBackEntrada);

        entradaVeiculos.add(panelEntrada);

    }

    private void initContabilidade(){
        //LAYOUT CONTROLE DA CONTABILIDADE
        contabilidade.setLayout(new BorderLayout());
        contabilidade.setSize(frameDimension);
        contabilidade.setResizable(false);
        contabilidade.setLocationRelativeTo(null);
        contabilidade.setTitle("Contabilidade");
        contabArea.setEditable(false);
        contabArea.append("String de teste");


        panelContabilidade.add(buttonOKContabilidade);
        contabilidade.add(contabArea);
        contabilidade.add(panelContabilidade,BorderLayout.SOUTH);

    }

    private void initPagamento(){

        pagamento.setLayout(new BorderLayout());
        pagamento.setSize(popupDimension);
        pagamento.setResizable(false);
        pagamento.setLocationRelativeTo(null);
        pagamento.setTitle("Pagamento");
        infoPlaca.setEditable(false);
        infoTipo.setEditable(false);
        infoPreco.setEditable(false);
        panelPagamento.setLayout(new GridLayout(7,0,10,10));
        panelPagamento.add(new JLabel("Placa"));
        panelPagamento.add(infoPlaca);
        panelPagamento.add(new JLabel("Tipo"));
        panelPagamento.add(infoTipo);
        panelPagamento.add(new JLabel("Valor"));
        panelPagamento.add(infoPreco);
        panelPagamento.add(buttonOKPagamento);
        buttonOKPagamento.addActionListener(e -> pagamento.dispose());

        pagamento.add(panelPagamento,BorderLayout.CENTER);

    }


    //singleton
    synchronized static Interface getInstance(){

        if(instance == null){
            instance = new Interface();
        }
        return instance;
    }



    private class StatusHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == buttonEntrada){
                placaEntrada.setText("");
                horaEntrada.setText(new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis())));
                entradaVeiculos.setVisible(true);
            }else if(e.getSource() == buttonSaida){
                placaSaida.setText("");
                horaSaida.setText(new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis())));
                saidaVeiculos.setVisible(true);
            }else if(e.getSource() == buttonContabilidade){
                contabilidade.setVisible(true);
            }else if(e.getSource() == buttonSetup){
                setupEstacionamento.setVisible(true);
            }else if(e.getSource() == buttonExit){
                if(JOptionPane.showConfirmDialog
                        (null,"Tem certeza?",
                                "Sair",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                {
                    System.exit(0);

                }

            } /*else if(e.getSource() == buttonTerreo) {
            	motoStatus.setVisible(true);
            	caminhoneteStatus.setVisible(true);
            	carroStatus.setVisible(false);
            	
            	
            } else if(e.getSource() == buttonPriPiso) {
            	carroStatus.setVisible(true);
            	motoStatus.setVisible(false);
            	caminhoneteStatus.setVisible(false);
            	
            }*/ 

        }
    }

    private class SetupHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == buttonOKSetup){
                if(precoCaminhonete.getText().equals("0.0")){
                    precoCaminhonete.setText("");
                }if (precoCarro.getText().equals("0.0")){
                    precoCarro.setText("");
                }if(precoMotocicleta.getText().equals("0.0")){
                    precoCaminhonete.setText("");
                }
                if(precoCaminhonete.getText().isEmpty() || precoCarro.getText().isEmpty() || precoMotocicleta.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Preencha todos os campos!","ERRO",JOptionPane.ERROR_MESSAGE);
                    precoCarro.setText(Double.toString(sistema.getPrecoCarro()));
                    precoCaminhonete.setText(Double.toString(sistema.getPrecoCaminhonete()));
                    precoMotocicleta.setText(Double.toString(sistema.getPrecoMoto()));

                }else{

                    sistema.setPrecoCaminhonete(Double.parseDouble(precoCaminhonete.getText()));
                    sistema.setPrecoCarro(Double.parseDouble(precoCarro.getText()));
                    sistema.setPrecoMoto(Double.parseDouble(precoMotocicleta.getText()));
                    JOptionPane.showMessageDialog(null,"Os preÃ§os digitados foram salvos!","INFO",JOptionPane.INFORMATION_MESSAGE);
                    setupEstacionamento.dispose();
                }
            } else if (e.getSource() == buttonClearSetup) {
                //limpa os campos
                precoCaminhonete.setText("");
                precoCarro.setText("");
                precoMotocicleta.setText("");
            }

        }
    }

    private class EntradaHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == buttonOKEntrada) {

                try {
                    if(placaEntrada.getText().isEmpty() || horaEntrada.getText().isEmpty()){
                        throw new ValorInvalidoException();
                    }
                    switch (Objects.requireNonNull(categoria.getSelectedItem()).toString()) {
                        case "Carro":
                            sistema.registraEntrada(new Automovel(placaEntrada.getText(), Automovel.CARRO), horaEntrada.getText());
                            break;
                        case "Caminhonete":
                            sistema.registraEntrada(new Automovel(placaEntrada.getText(), Automovel.CAMINHONETE), horaEntrada.getText());
                            break;
                        case "Moto":
                            sistema.registraEntrada(new Automovel(placaEntrada.getText(), Automovel.MOTO), horaEntrada.getText());
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "SELECIONE O TIPO DE VEICULO", "ERRO", JOptionPane.WARNING_MESSAGE);
                            break;
                    }
                    entradaVeiculos.dispose();

                } catch (WriteFileException e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage(), "ERRO AO ESCREVER NO ARQUIVO", JOptionPane.ERROR_MESSAGE);
                } catch (ValorInvalidoException e2) {
                    JOptionPane.showMessageDialog(null, "DIGITE UM VALOR VALIDO", "ERRO", JOptionPane.ERROR_MESSAGE);
                } catch (VagaOcupadaException e3) {
                    JOptionPane.showMessageDialog(null, "VEICULO JA CADASTRADO", "ERRO", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == buttonBackEntrada) {
                entradaVeiculos.dispose();
            }

        }
    }


    private class SaidaHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == buttonOKSaida){
                try{
                    if(placaSaida.getText().isEmpty() || horaSaida.getText().isEmpty()){
                        throw new ValorInvalidoException();
                    }
                    double aPagar = sistema.registraSaida(new Automovel(placaSaida.getText(),Automovel.CARRO),horaSaida.getText());
                    infoPlaca.setText(placaSaida.getText());
                    infoPreco.setText(Double.toString(aPagar));
                    infoTipo.setText("Carro");
                    saidaVeiculos.dispose();
                    pagamento.setVisible(true);

                }catch (WriteFileException e1){
                    JOptionPane.showMessageDialog(null, e1.getMessage(), "ERRO AO ESCREVER NO ARQUIVO", JOptionPane.ERROR_MESSAGE);
                }catch (ReadFileException e2){
                    JOptionPane.showMessageDialog(null, e2.getMessage(), "ERRO AO LER O ARQUIVO", JOptionPane.ERROR_MESSAGE);
                }catch (PlacaInexistenteException e3){
                    JOptionPane.showMessageDialog(null, "PLACA INEXISTENTE", "ERRO", JOptionPane.ERROR_MESSAGE);
                } catch (ValorInvalidoException e4) {
                    JOptionPane.showMessageDialog(null, "DIGITE UM VALOR VALIDO", "ERRO", JOptionPane.ERROR_MESSAGE);

                }
            }else if(e.getSource() == buttonBackSaida){
                saidaVeiculos.dispose();
            }
        }
    }

    private class ContabileHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == buttonOKContabilidade){
                contabilidade.dispose();
            }
        }
    }
}
