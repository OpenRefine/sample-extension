# 📌 Chat Completion Prompt Guide

## 🔹 General Structure
A well-structured prompt improves the quality of responses from an LLM.


## 🔹 Key Components

| Field        | Description |
|-------------|------------|
| `model` | The LLM model to use (e.g., `gpt-4`, `mistralai/Mistral-7B-Instruct`). |
| `messages` | The conversation history, structured as an array of `{ role, content }` objects. |
| `temperature` | Controls randomness (lower = more deterministic). |
| `max_tokens` | Limits the number of tokens in the response. |

---

## 🔹 Roles in Messages
1. **System (`"role": "system"`)**  
   - Defines behavior and context of the assistant.  
   - Example:  

2. **User (`"role": "user"`)**  
- Represents the input from the user.  
- Example:  

3. **Assistant (`"role": "assistant"`)**  
- Optionally stores previous responses for better continuity.  
- Example:  

{
  "model": "gpt-4",
  "messages": [
    { "role": "system", "content": "You are an AI acting as a medieval knight." },
    { "role": "user", "content": "How would you prepare for battle?" }
  ]
}


🔹 Parameter Tuning Recommendations
Parameter	Effect
temperature	0.0 - 1.0 → Lower values (0.2) = more predictable, higher values (0.9) = more creative.
top_p	0.0 - 1.0 → Alternative to temperature, filters responses probabilistically.
max_tokens	Limits response length. Higher values return longer responses.
frequency_penalty	Reduces repetition in responses (-2.0 to 2.0).
presence_penalty	Encourages new topics (-2.0 to 2.0).
