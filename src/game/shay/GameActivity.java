package game.shay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import game.shay.charadaseenigmas.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	
	private TextView charada;
	private EditText editText;
	private Button enviar;
	
	private Toast toast;
	private View layoutDR;
	private TextView toastDRText;
	private View layoutINF;
	private TextView toastINFText;
	
	private ArrayList<String> perguntas;
	private ArrayList<String> respostas;
	
	private StringBuilder dicaTxt;
	private int counter;
	private int numero;
	
	private static final String[] CARACTERES_ESPECIAIS = {
		"Á","á","Â","â","À","à","Ã","ã","É","é","Ê","ê",
		"È","è","Í","í","Î","î","Ì","ì","Ó","ó","Ô","ô","Ò","ò",
		"Õ","õ","Ú","ú","Û","û","Ù","ù","Ü","ü","Ç","ç"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		perguntas = new ArrayList<String>();
		respostas = new ArrayList<String>();
		
		handleTextFile();
		
		charada = (TextView) findViewById(R.id.charada);
		editText = (EditText) findViewById(R.id.editText);
		enviar = (Button) findViewById(R.id.enviar);

		changeAnswer();
		
		layoutDR = getLayout(R.layout.toast_dr, R.id.toastDR_layout);
		toastDRText = (TextView) layoutDR.findViewById(R.id.toastDRText);
		
		layoutINF = getLayout(R.layout.toast_inf, R.id.toastINF_layout);
		toastINFText = (TextView) layoutINF.findViewById(R.id.toastINFText);

		if (Build.VERSION.SDK_INT < 11) {
			toast = new Toast(this.getApplicationContext());
		}
		
		if(savedInstanceState != null) {
			numero = savedInstanceState.getInt("numero");
			counter = savedInstanceState.getInt("counter");
			dicaTxt.append(savedInstanceState.getString("dicaTxt"));
			
			charada.setText(perguntas.get(numero));
		} 
	}

	private View getLayout(int layoutResource, int idLayout) {
		LayoutInflater inflater = getLayoutInflater();
		return inflater.inflate(layoutResource, (ViewGroup) findViewById(idLayout));
	}
	
	private void changeToast(boolean isDR) {
		if(toast != null) {
			toast.cancel();
		}
		
		if (Build.VERSION.SDK_INT >= 11) {
			toast = new Toast(this.getApplicationContext());
		}
		
		if(isDR) {
			toast.setGravity(Gravity.FILL_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layoutDR);
		} else {
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layoutINF);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
		}
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		editText.clearFocus();
	}

	public void dica(View view) {
		changeToast(true);
		
		String resposta = respostas.get(numero);
		int leng = resposta.length();
		int dicas = 0;

		if(leng >= 3) {
			dicas = leng/2;
		}
		
		if(counter <= dicas) {
			
			if(dicas == 0 || counter == 0) {
				for(int i=1;i<=leng;i++) {
					if(resposta.charAt(i-1) != ' ') {
						dicaTxt.append("_");
					} else {
						dicaTxt.append(" ");
					}
					
					if(i == leng) {
						break;
					}
					dicaTxt.append(" "); 
				}
				counter++;
			} else {
				/*
				 *	Tabela substituição( começa do 0 / com espaços) 
				 * 
				 * 	Quero substituir:   |   Então, escolho a posição:
				 * 
				 * 	0                   |	0   (0*2)=0
				 * 	1					|	2	(1*2)=2
				 * 	2					|	4	(2*2)=4
				 * 	3					|	6	(3*2)=6
				*/
				Random rand = new Random();
				int num = rand.nextInt(leng - 1);
				
				while(dicaTxt.charAt(num * 2) != '_') {
					num = rand.nextInt(leng - 1);
				}
				
				char letra = resposta.charAt(num);
				dicaTxt.setCharAt(num * 2, letra);
				
				counter++;
			}
			
		}

		toastDRText.setText(leng + " letra(s): " + dicaTxt.toString());
		toast.show();
	}

	public void resposta(View view) {
		changeToast(true);
		
		toastDRText.setText("R: " + respostas.get(numero));
		toast.show();
		hideKeyboard();
		changeStateTools(false);
		
		/*new Thread(new Runnable() {
			public void run() {
				try {
					//Por algum motivo está indo mais rápido que o Toast
					int dur = toast.getDuration() + 1;
					while(dur > 0) {
						Thread.sleep(1000);
						dur--;
					}
					runOnUiThread(new Runnable() {
						public void run() {
							changeAnswer();
						}
					});
					
				} catch (InterruptedException e) {}
			}
		}).start();*/
	}

	public void proxima(View view) {
		if(toast != null) {
			toast.cancel();
		}
		
		hideKeyboard();
		changeAnswer();
		changeStateTools(true);
	}

	@SuppressLint("DefaultLocale") 
	public void enviar(View view) {
		changeToast(false);
		
		String digitado = editText.getText().toString().trim();
		String resposta = respostas.get(numero);
		
		if(digitado.equalsIgnoreCase(resposta)) {
			toastINFText.setText("Você acertou");
			
			layoutINF.setBackgroundResource(R.drawable.border_green);
			toast.setView(layoutINF);
			
			toast.show();
			
			hideKeyboard();
			changeAnswer();
		} else {
			boolean acent = false;
			boolean perto = false;
			
			int tamanhoResposta = resposta.length();
			
			if(tamanhoResposta >= 4) {
				if(digitado.length() >= tamanhoResposta - 2 && digitado.length() <= tamanhoResposta + 2) {
					ArrayList<String> partes = new ArrayList<String>();
					int expressao = 0;

					if(tamanhoResposta%2 == 0) {
						expressao = tamanhoResposta/2;
					} else {
						expressao = tamanhoResposta/2 + 1;
					}

					for(int i=0;i<tamanhoResposta;i++) {
						if(i <= expressao) {
							partes.add(resposta.substring(i, tamanhoResposta/2 + i));
						}
					}

					for(int i=0;i<partes.size();i++) {
						if(digitado.toLowerCase().contains(partes.get(i).toLowerCase())) {
							perto = true;
						}
					}
				}
			} 
			
			if(tamanhoResposta == digitado.length()) {
				ArrayList<Integer> posicao = new ArrayList<Integer>();
				ArrayList<Integer> index = new ArrayList<Integer>();
				
				for(int i=0;i<CARACTERES_ESPECIAIS.length;i++) {
					if(resposta.contains(CARACTERES_ESPECIAIS[i])) {
						posicao.add(i);
						index.add(resposta.indexOf(CARACTERES_ESPECIAIS[i]));
					}
				}
				
				for(int i=0;i<posicao.size();i++) {
					if(posicao.get(i) >= 0 && posicao.get(i) <= 7) {
						if(digitado.charAt(index.get(i)) == 'a' || digitado.charAt(index.get(i)) == 'A') {
							acent = true;
							break;
						}
					} else if(posicao.get(i) >= 8 && posicao.get(i) <= 13) {
						if(digitado.charAt(index.get(i)) == 'e' || digitado.charAt(index.get(i)) == 'E') {
							acent = true;
							break;
						}
					} else if(posicao.get(i) >= 14 && posicao.get(i) <= 19) {
						if(digitado.charAt(index.get(i)) == 'i' || digitado.charAt(index.get(i)) == 'I') {
							acent = true;
							break;
						}
					} else if(posicao.get(i) >= 20 && posicao.get(i) <= 27) {
						if(digitado.charAt(index.get(i)) == 'o' || digitado.charAt(index.get(i)) == 'O') {
							acent = true;
							break;
						}
					} else if(posicao.get(i) >= 28 && posicao.get(i) <= 35) {
						if(digitado.charAt(index.get(i)) == 'u' || digitado.charAt(index.get(i)) == 'U') {
							acent = true;
							break;
						}
					} else {
						if(digitado.charAt(index.get(i)) == 'c' || digitado.charAt(index.get(i)) == 'C') {
							acent = true;
							break;
						}
					}
				}
			}
			
			if(acent) {
				toastINFText.setText("Você está perto. A palavra foi escrita corretamente?");
				
				layoutINF.setBackgroundResource(R.drawable.border_orange);
				toast.setView(layoutINF);
				
				toast.show();
			} else if(perto) {
				toastINFText.setText("Você está perto");
			
				layoutINF.setBackgroundResource(R.drawable.border_orange);
				toast.setView(layoutINF);
				
				toast.show();
			} else {
				toastINFText.setText("Você errou");
				
				layoutINF.setBackgroundResource(R.drawable.border_red);
				toast.setView(layoutINF);
				
				toast.show();
			}
		}
	}
	
	private void handleTextFile() {
		BufferedReader reader = null;
		String txt = null;
		String aux = null;
		
		try {
			InputStream is = getResources().openRawResource(R.raw.charadas);
			reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
			
			while((aux = reader.readLine()) != null) {
				txt+=aux;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
		//Ímpar - pergunta
		//Par - resposta
		String[] charadas = txt.split("#");
		for(int i=1;i<charadas.length;i++) {
			if(i%2 != 0) {
				perguntas.add(charadas[i]);
			} else {
				respostas.add(charadas[i]);
			}
		}
		
	}
	
	private void changeAnswer() {
		counter = 0;
		dicaTxt = new StringBuilder();
		
		numero = getNumero() > perguntas.size()-1 ? 0 : getNumero();
		updateNumero(numero+1);

		charada.setText(perguntas.get(numero));
		editText.setText("");
	}
	
	private void hideKeyboard() {   
	    // Check if no view has focus:
		// Verifica se o editText ta focado (o teclado irá aparecer se estiver focado)
	    View view = this.getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}

	private void changeStateTools(boolean isEnabled){
		editText.setEnabled(isEnabled);
		enviar.setEnabled(isEnabled);
	}
	
	private int getNumero() {
		DBHelper dbHelper = new DBHelper(this);
		return dbHelper.getNumero();
	}
	
	private void updateNumero(int numero) {
		DBHelper dbHelper = new DBHelper(this);
		dbHelper.updateNumero(numero);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("numero", numero);
		outState.putInt("counter", counter);
		outState.putString("dicaTxt", dicaTxt.toString());
	}
}
