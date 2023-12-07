package my.edu.tarc.moneymate.Goal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R

class GoalsAdapter(private var goal: List<Goal>,private val onGoalClick: (Goal) -> Unit,
                   private val onGoalLongClick: (Goal) -> Unit):RecyclerView.Adapter<GoalsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvGoalTitle)
        private val targetDateTextView: TextView = itemView.findViewById(R.id.goal_targetDate)
        private val savedAmountTextView: TextView = itemView.findViewById(R.id.goalSavedMoney)
        private val targetAmountTextView: TextView = itemView.findViewById(R.id.goal_targetAmount)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress)
        @SuppressLint("SetTextI18n")
        fun bind(goal: Goal)
        {
            titleTextView.text = goal.title
            targetDateTextView.text = "Target Date: ${goal.desiredDate}"
            savedAmountTextView.text = "Saved: RM ${goal.savedAmount}"
            targetAmountTextView.text = "Goal: RM ${goal.targetAmount}"

            if (goal.targetAmount > 0) {
                val progress = (goal.savedAmount.toFloat() / goal.targetAmount * 100).toInt()
                progressBar.progress = progress
                updateProgressBarColor(progressBar,progress)
            } else {
                progressBar.progress = 0
            }
        }
        private fun updateProgressBarColor(progressBar: ProgressBar, progress: Int) {
            val colorRes = when {
                progress < 10 -> R.color.Red
                progress < 60 -> R.color.color2
                else -> R.color.color4
            }

            progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(itemView.context, colorRes),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return goal.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goal[position]
        holder.bind(goal)
        // Setting click listener
        holder.itemView.setOnClickListener {
            onGoalClick(goal)
        }

        // Setting long click listener
        holder.itemView.setOnLongClickListener {
            onGoalLongClick(goal)
            true
        }
    }
    fun updateGoals(newGoals: List<Goal>) {
        goal = newGoals
        notifyDataSetChanged()
    }
}