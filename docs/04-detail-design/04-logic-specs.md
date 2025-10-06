# Logic Specifications - RepeatWise

## 1. Overview

Logic Specifications mô tả chi tiết các thuật toán, pseudo-code và business logic phức tạp trong RepeatWise. Tài liệu này cung cấp thông tin đầy đủ để developer implement các tính năng core của hệ thống.

## 2. Spaced Repetition System (SRS) Algorithm

### 2.1 SRS Algorithm Overview

SRS Algorithm là core logic của RepeatWise, quyết định thời gian ôn tập tiếp theo dựa trên hiệu suất học tập của user.

#### 2.1.1 Input Parameters
- **Score**: Điểm số từ 0-100
- **Word Count**: Số từ trong set
- **Cycle Number**: Số thứ tự cycle (1-5)
- **Previous Delay**: Thời gian delay của cycle trước
- **User Performance**: Lịch sử hiệu suất của user

#### 2.1.2 Output
- **Next Cycle Delay**: Số ngày delay cho cycle tiếp theo
- **Difficulty Adjustment**: Điều chỉnh độ khó
- **Status Update**: Cập nhật trạng thái set

### 2.2 SRS Algorithm Pseudo-code

```pseudocode
FUNCTION calculateNextCycleDelay(score, wordCount, cycleNumber, previousDelay, userPerformance)
    // Base delay calculation
    baseDelay = getBaseDelay(cycleNumber)
    
    // Score factor calculation
    scoreFactor = calculateScoreFactor(score)
    
    // Word count factor
    wordCountFactor = calculateWordCountFactor(wordCount)
    
    // User performance factor
    performanceFactor = calculatePerformanceFactor(userPerformance)
    
    // Calculate final delay
    finalDelay = baseDelay * scoreFactor * wordCountFactor * performanceFactor
    
    // Apply constraints
    finalDelay = max(minDelay, min(maxDelay, finalDelay))
    
    RETURN finalDelay
END FUNCTION

FUNCTION getBaseDelay(cycleNumber)
    SWITCH cycleNumber
        CASE 1: RETURN 1    // 1 day
        CASE 2: RETURN 3    // 3 days
        CASE 3: RETURN 7    // 1 week
        CASE 4: RETURN 14   // 2 weeks
        CASE 5: RETURN 30   // 1 month
        DEFAULT: RETURN 30
    END SWITCH
END FUNCTION

FUNCTION calculateScoreFactor(score)
    IF score >= 90 THEN
        RETURN 1.5  // Excellent performance
    ELSE IF score >= 80 THEN
        RETURN 1.2  // Good performance
    ELSE IF score >= 70 THEN
        RETURN 1.0  // Average performance
    ELSE IF score >= 60 THEN
        RETURN 0.8  // Below average
    ELSE
        RETURN 0.5  // Poor performance
    END IF
END FUNCTION

FUNCTION calculateWordCountFactor(wordCount)
    // More words = longer delay
    IF wordCount <= 10 THEN
        RETURN 0.8
    ELSE IF wordCount <= 20 THEN
        RETURN 1.0
    ELSE IF wordCount <= 50 THEN
        RETURN 1.2
    ELSE
        RETURN 1.5
    END IF
END FUNCTION

FUNCTION calculatePerformanceFactor(userPerformance)
    averageScore = calculateAverageScore(userPerformance)
    consistency = calculateConsistency(userPerformance)
    
    // Combine average score and consistency
    factor = (averageScore / 100) * consistency
    
    RETURN max(0.5, min(2.0, factor))
END FUNCTION
```

### 2.3 SRS Algorithm Implementation Details

#### 2.3.1 Score Factor Calculation
```pseudocode
FUNCTION calculateScoreFactor(score)
    // Exponential decay for low scores
    IF score < 60 THEN
        factor = 0.5 + (score / 60) * 0.3
    // Linear growth for medium scores
    ELSE IF score < 80 THEN
        factor = 0.8 + (score - 60) / 20 * 0.4
    // Exponential growth for high scores
    ELSE
        factor = 1.2 + (score - 80) / 20 * 0.3
    END IF
    
    RETURN factor
END FUNCTION
```

#### 2.3.2 Word Count Factor Calculation
```pseudocode
FUNCTION calculateWordCountFactor(wordCount)
    // Logarithmic scaling for word count
    factor = 1 + log(wordCount / 10) * 0.2
    
    // Apply bounds
    factor = max(0.5, min(2.0, factor))
    
    RETURN factor
END FUNCTION
```

#### 2.3.3 Performance Factor Calculation
```pseudocode
FUNCTION calculatePerformanceFactor(userPerformance)
    // Calculate rolling average of last 10 cycles
    recentScores = getRecentScores(userPerformance, 10)
    averageScore = calculateAverage(recentScores)
    
    // Calculate consistency (lower standard deviation = higher consistency)
    standardDeviation = calculateStandardDeviation(recentScores)
    consistency = 1 - (standardDeviation / 100)
    
    // Combine factors
    factor = (averageScore / 100) * consistency
    
    RETURN max(0.5, min(2.0, factor))
END FUNCTION
```

## 3. Cycle Management Logic

### 3.1 Cycle Creation Logic

```pseudocode
FUNCTION createInitialCycles(setId, wordCount)
    cycles = []
    
    FOR i = 1 TO 5 DO
        cycle = {
            id: generateUUID(),
            setId: setId,
            cycleNumber: i,
            startDate: calculateStartDate(i),
            status: 'pending',
            scheduledAt: calculateScheduledAt(i)
        }
        
        cycles.add(cycle)
    END FOR
    
    RETURN cycles
END FUNCTION

FUNCTION calculateStartDate(cycleNumber)
    currentDate = getCurrentDate()
    
    SWITCH cycleNumber
        CASE 1: RETURN currentDate
        CASE 2: RETURN currentDate + 1 day
        CASE 3: RETURN currentDate + 3 days
        CASE 4: RETURN currentDate + 7 days
        CASE 5: RETURN currentDate + 14 days
    END SWITCH
END FUNCTION

FUNCTION calculateScheduledAt(cycleNumber)
    startDate = calculateStartDate(cycleNumber)
    userTime = getUserDefaultReminderTime()
    
    RETURN combineDateTime(startDate, userTime)
END FUNCTION
```

### 3.2 Cycle Progression Logic

```pseudocode
FUNCTION progressToNextCycle(currentCycle, score)
    // Update current cycle
    currentCycle.status = 'completed'
    currentCycle.completedAt = getCurrentTimestamp()
    currentCycle.averageScore = score
    
    // Calculate next cycle delay
    nextDelay = calculateNextCycleDelay(
        score,
        currentCycle.set.wordCount,
        currentCycle.cycleNumber,
        currentCycle.previousDelay,
        getUserPerformance(currentCycle.set.userId)
    )
    
    // Update set status
    set = currentCycle.set
    set.currentCycle = set.currentCycle + 1
    set.totalReviews = set.totalReviews + 1
    set.averageScore = calculateSetAverageScore(set)
    set.lastReviewedAt = getCurrentTimestamp()
    
    // Check if set is completed
    IF set.currentCycle > 5 THEN
        set.status = 'mastered'
        RETURN 'completed'
    END IF
    
    // Schedule next cycle
    nextCycle = findNextCycle(set.id, set.currentCycle)
    IF nextCycle != null THEN
        nextCycle.scheduledAt = getCurrentTimestamp() + nextDelay days
        nextCycle.status = 'pending'
    END IF
    
    RETURN 'progressed'
END FUNCTION
```

### 3.3 Cycle Status Management

```pseudocode
FUNCTION updateCycleStatus(cycleId, newStatus, score)
    cycle = getCycleById(cycleId)
    
    IF cycle == null THEN
        RETURN 'not_found'
    END IF
    
    // Validate status transition
    IF !isValidStatusTransition(cycle.status, newStatus) THEN
        RETURN 'invalid_transition'
    END IF
    
    // Update cycle
    cycle.status = newStatus
    cycle.updatedAt = getCurrentTimestamp()
    
    IF newStatus == 'completed' THEN
        cycle.completedAt = getCurrentTimestamp()
        cycle.averageScore = score
        
        // Update set statistics
        updateSetStatistics(cycle.setId, score)
        
        // Progress to next cycle
        progressToNextCycle(cycle, score)
        
    ELSE IF newStatus == 'skipped' THEN
        cycle.skippedAt = getCurrentTimestamp()
        cycle.skipReason = getSkipReason()
        
        // Reschedule cycle
        rescheduleCycle(cycle)
    END IF
    
    // Log activity
    logActivity('cycle_status_update', cycle)
    
    RETURN 'success'
END FUNCTION

FUNCTION isValidStatusTransition(currentStatus, newStatus)
    validTransitions = {
        'pending': ['in_progress', 'skipped'],
        'in_progress': ['completed', 'skipped'],
        'completed': [],
        'skipped': ['in_progress']
    }
    
    RETURN newStatus IN validTransitions[currentStatus]
END FUNCTION
```

## 4. Reminder Management Logic

### 4.1 Reminder Calculation Logic

```pseudocode
FUNCTION calculateReminders(userId, date)
    reminders = []
    
    // Get user settings
    userSettings = getUserSettings(userId)
    dailyLimit = userSettings.dailyReminderLimit
    
    // Get active sets
    activeSets = getActiveSets(userId)
    
    // Get pending cycles for the date
    pendingCycles = getPendingCyclesForDate(userId, date)
    
    // Sort by priority
    pendingCycles = sortByPriority(pendingCycles)
    
    // Limit to daily limit
    selectedCycles = pendingCycles.take(dailyLimit)
    
    FOR EACH cycle IN selectedCycles DO
        reminder = {
            id: generateUUID(),
            userId: userId,
            setId: cycle.setId,
            cycleId: cycle.id,
            scheduledDate: date,
            reminderTime: getUserDefaultReminderTime(userId),
            status: 'pending',
            type: 'push'
        }
        
        reminders.add(reminder)
    END FOR
    
    RETURN reminders
END FUNCTION

FUNCTION sortByPriority(cycles)
    // Priority calculation
    FOR EACH cycle IN cycles DO
        priority = calculatePriority(cycle)
        cycle.priority = priority
    END FOR
    
    // Sort by priority (higher priority first)
    RETURN cycles.sortBy(priority, DESC)
END FUNCTION

FUNCTION calculatePriority(cycle)
    priority = 0
    
    // Base priority by cycle number
    priority += cycle.cycleNumber * 10
    
    // Overdue penalty
    IF cycle.scheduledAt < getCurrentTimestamp() THEN
        daysOverdue = (getCurrentTimestamp() - cycle.scheduledAt) / DAYS
        priority += daysOverdue * 20
    END IF
    
    // Set importance
    IF cycle.set.category == 'vocabulary' THEN
        priority += 5
    ELSE IF cycle.set.category == 'grammar' THEN
        priority += 3
    END IF
    
    // User performance factor
    userPerformance = getUserPerformance(cycle.set.userId)
    IF userPerformance.averageScore < 70 THEN
        priority += 10  // Boost priority for struggling users
    END IF
    
    RETURN priority
END FUNCTION
```

### 4.2 Reminder Overload Prevention

```pseudocode
FUNCTION preventReminderOverload(userId, date)
    // Get existing reminders for the date
    existingReminders = getRemindersForDate(userId, date)
    
    // Get user settings
    userSettings = getUserSettings(userId)
    dailyLimit = userSettings.dailyReminderLimit
    
    // Check if limit exceeded
    IF existingReminders.count >= dailyLimit THEN
        RETURN 'limit_exceeded'
    END IF
    
    // Check for time conflicts
    conflicts = findTimeConflicts(existingReminders, date)
    IF conflicts.count > 0 THEN
        RETURN 'time_conflicts'
    END IF
    
    RETURN 'ok'
END FUNCTION

FUNCTION findTimeConflicts(existingReminders, date)
    conflicts = []
    
    FOR EACH reminder IN existingReminders DO
        // Check for overlapping time slots
        FOR EACH otherReminder IN existingReminders DO
            IF reminder.id != otherReminder.id THEN
                IF isTimeOverlapping(reminder.reminderTime, otherReminder.reminderTime) THEN
                    conflicts.add({
                        reminder1: reminder,
                        reminder2: otherReminder,
                        conflictType: 'time_overlap'
                    })
                END IF
            END IF
        END FOR
    END FOR
    
    RETURN conflicts
END FUNCTION

FUNCTION isTimeOverlapping(time1, time2)
    // Check if two time slots overlap (within 1 hour)
    timeDiff = abs(time1 - time2)
    RETURN timeDiff < 1 HOUR
END FUNCTION
```

## 5. Statistics Calculation Logic

### 5.1 Learning Progress Calculation

```pseudocode
FUNCTION calculateLearningProgress(userId, period)
    // Get data for the period
    startDate = getPeriodStartDate(period)
    endDate = getPeriodEndDate(period)
    
    // Get user's sets and cycles
    sets = getSetsByUser(userId)
    cycles = getCyclesByUserAndPeriod(userId, startDate, endDate)
    
    // Calculate basic statistics
    totalSets = sets.count
    activeSets = sets.filter(status == 'active').count
    completedSets = sets.filter(status == 'mastered').count
    
    totalCycles = cycles.count
    completedCycles = cycles.filter(status == 'completed').count
    
    // Calculate average score
    completedCyclesWithScore = cycles.filter(status == 'completed' AND averageScore != null)
    averageScore = calculateAverage(completedCyclesWithScore.map(cycle => cycle.averageScore))
    
    // Calculate study streak
    studyStreak = calculateStudyStreak(userId, endDate)
    
    // Calculate learning velocity
    learningVelocity = calculateLearningVelocity(cycles, period)
    
    RETURN {
        totalSets: totalSets,
        activeSets: activeSets,
        completedSets: completedSets,
        totalCycles: totalCycles,
        completedCycles: completedCycles,
        averageScore: averageScore,
        studyStreak: studyStreak,
        learningVelocity: learningVelocity,
        period: period
    }
END FUNCTION

FUNCTION calculateStudyStreak(userId, endDate)
    streak = 0
    currentDate = endDate
    
    WHILE currentDate >= getPeriodStartDate('all') DO
        // Check if user had activity on this date
        hasActivity = hasUserActivityOnDate(userId, currentDate)
        
        IF hasActivity THEN
            streak++
            currentDate = currentDate - 1 day
        ELSE
            BREAK
        END IF
    END WHILE
    
    RETURN streak
END FUNCTION

FUNCTION calculateLearningVelocity(cycles, period)
    // Calculate cycles completed per day
    totalDays = getPeriodDays(period)
    completedCycles = cycles.filter(status == 'completed').count
    
    velocity = completedCycles / totalDays
    
    RETURN velocity
END FUNCTION
```

### 5.2 Set Statistics Calculation

```pseudocode
FUNCTION calculateSetStatistics(setId)
    set = getSetById(setId)
    cycles = getCyclesBySet(setId)
    
    // Basic cycle statistics
    totalCycles = cycles.count
    completedCycles = cycles.filter(status == 'completed').count
    
    // Score statistics
    completedCyclesWithScore = cycles.filter(status == 'completed' AND averageScore != null)
    scores = completedCyclesWithScore.map(cycle => cycle.averageScore)
    
    averageScore = calculateAverage(scores)
    bestScore = max(scores)
    worstScore = min(scores)
    
    // Study time calculation
    studyTime = calculateTotalStudyTime(cycles)
    
    // Progress calculation
    progress = (completedCycles / totalCycles) * 100
    
    // Difficulty trend
    difficultyTrend = calculateDifficultyTrend(cycles)
    
    // Performance trend
    performanceTrend = calculatePerformanceTrend(cycles)
    
    RETURN {
        setId: setId,
        totalCycles: totalCycles,
        completedCycles: completedCycles,
        averageScore: averageScore,
        bestScore: bestScore,
        worstScore: worstScore,
        studyTime: studyTime,
        progress: progress,
        difficultyTrend: difficultyTrend,
        performanceTrend: performanceTrend
    }
END FUNCTION

FUNCTION calculateTotalStudyTime(cycles)
    totalTime = 0
    
    FOR EACH cycle IN cycles DO
        IF cycle.status == 'completed' THEN
            // Estimate study time based on word count and score
            wordCount = cycle.set.wordCount
            score = cycle.averageScore
            
            // Base time: 1 minute per word
            baseTime = wordCount * 1
            
            // Adjust based on score (lower score = more time)
            timeMultiplier = 1 + (100 - score) / 100 * 0.5
            
            cycleTime = baseTime * timeMultiplier
            totalTime += cycleTime
        END IF
    END FOR
    
    RETURN totalTime
END FUNCTION
```

## 6. Data Export/Import Logic

### 6.1 Data Export Logic

```pseudocode
FUNCTION exportUserData(userId, exportType, format)
    // Validate export type
    IF !isValidExportType(exportType) THEN
        RETURN 'invalid_export_type'
    END IF
    
    // Get user data
    userData = getUserData(userId)
    
    // Filter data based on export type
    filteredData = filterDataByType(userData, exportType)
    
    // Generate export file
    exportFile = generateExportFile(filteredData, format)
    
    // Compress file if needed
    IF exportFile.size > MAX_FILE_SIZE THEN
        exportFile = compressFile(exportFile)
    END IF
    
    // Store file
    fileUrl = storeFile(exportFile)
    
    // Create export record
    exportRecord = {
        id: generateUUID(),
        userId: userId,
        exportType: exportType,
        format: format,
        fileUrl: fileUrl,
        fileSize: exportFile.size,
        status: 'completed',
        createdAt: getCurrentTimestamp()
    }
    
    saveExportRecord(exportRecord)
    
    // Send notification
    sendExportNotification(userId, fileUrl)
    
    RETURN exportRecord
END FUNCTION

FUNCTION filterDataByType(userData, exportType)
    SWITCH exportType
        CASE 'all':
            RETURN userData
        CASE 'sets':
            RETURN {
                sets: userData.sets,
                setItems: userData.setItems,
                learningCycles: userData.learningCycles,
                reviewHistories: userData.reviewHistories
            }
        CASE 'statistics':
            RETURN {
                statistics: userData.statistics,
                activityLogs: userData.activityLogs
            }
        CASE 'preferences':
            RETURN {
                userSettings: userData.userSettings,
                userProfile: userData.userProfile
            }
    END SWITCH
END FUNCTION
```

### 6.2 Data Import Logic

```pseudocode
FUNCTION importUserData(userId, importFile)
    // Validate file format
    IF !isValidImportFormat(importFile) THEN
        RETURN 'invalid_format'
    END IF
    
    // Parse file content
    parsedData = parseImportFile(importFile)
    
    // Validate data structure
    validationResult = validateImportData(parsedData)
    IF !validationResult.isValid THEN
        RETURN 'validation_failed'
    END IF
    
    // Check for conflicts
    conflicts = findImportConflicts(userId, parsedData)
    IF conflicts.count > 0 THEN
        RETURN 'conflicts_found'
    END IF
    
    // Create backup
    backupId = createBackup(userId)
    
    // Import data
    importResult = performImport(userId, parsedData)
    
    // Log import activity
    logImportActivity(userId, importResult)
    
    RETURN importResult
END FUNCTION

FUNCTION findImportConflicts(userId, importData)
    conflicts = []
    
    // Check for duplicate sets
    existingSets = getSetsByUser(userId)
    FOR EACH importedSet IN importData.sets DO
        FOR EACH existingSet IN existingSets DO
            IF importedSet.name == existingSet.name THEN
                conflicts.add({
                    type: 'duplicate_set',
                    importedSet: importedSet,
                    existingSet: existingSet,
                    resolution: 'rename'
                })
            END IF
        END FOR
    END FOR
    
    // Check for date conflicts
    FOR EACH importedCycle IN importData.learningCycles DO
        FOR EACH existingCycle IN getCyclesByUser(userId) DO
            IF isDateConflict(importedCycle.scheduledAt, existingCycle.scheduledAt) THEN
                conflicts.add({
                    type: 'date_conflict',
                    importedCycle: importedCycle,
                    existingCycle: existingCycle,
                    resolution: 'reschedule'
                })
            END IF
        END FOR
    END FOR
    
    RETURN conflicts
END FUNCTION
```

## 7. Performance Optimization Logic

### 7.1 Caching Strategy

```pseudocode
FUNCTION getCachedStatistics(userId, period)
    cacheKey = generateCacheKey('statistics', userId, period)
    
    // Check cache
    cachedData = getFromCache(cacheKey)
    IF cachedData != null AND !isCacheExpired(cachedData) THEN
        RETURN cachedData
    END IF
    
    // Calculate statistics
    statistics = calculateLearningProgress(userId, period)
    
    // Cache result
    cacheData = {
        data: statistics,
        timestamp: getCurrentTimestamp(),
        ttl: getCacheTTL('statistics')
    }
    
    setCache(cacheKey, cacheData)
    
    RETURN statistics
END FUNCTION

FUNCTION generateCacheKey(type, userId, period)
    RETURN type + ':' + userId + ':' + period + ':' + getCurrentDate()
END FUNCTION

FUNCTION getCacheTTL(type)
    SWITCH type
        CASE 'statistics': RETURN 1 HOUR
        CASE 'user_preferences': RETURN 30 MINUTES
        CASE 'set_list': RETURN 15 MINUTES
        DEFAULT: RETURN 5 MINUTES
    END SWITCH
END FUNCTION
```

### 7.2 Background Job Processing

```pseudocode
FUNCTION processBackgroundJob(jobId)
    job = getJobById(jobId)
    
    IF job == null THEN
        RETURN 'job_not_found'
    END IF
    
    // Update job status
    job.status = 'processing'
    job.startedAt = getCurrentTimestamp()
    updateJob(job)
    
    TRY
        // Process job based on type
        SWITCH job.type
            CASE 'export_data':
                result = exportUserData(job.userId, job.exportType, job.format)
            CASE 'import_data':
                result = importUserData(job.userId, job.importFile)
            CASE 'calculate_statistics':
                result = calculateLearningProgress(job.userId, job.period)
            CASE 'send_reminders':
                result = sendScheduledReminders(job.scheduledDate)
        END SWITCH
        
        // Update job status
        job.status = 'completed'
        job.completedAt = getCurrentTimestamp()
        job.result = result
        
    CATCH error
        // Update job status
        job.status = 'failed'
        job.failedAt = getCurrentTimestamp()
        job.error = error.message
        
    FINALLY
        updateJob(job)
    END TRY
    
    RETURN job.status
END FUNCTION
```

## 8. Validation Logic

### 8.1 Input Validation

```pseudocode
FUNCTION validateUserInput(input, inputType)
    errors = []
    
    SWITCH inputType
        CASE 'email':
            IF !isValidEmail(input) THEN
                errors.add('Invalid email format')
            END IF
            
        CASE 'password':
            IF !isValidPassword(input) THEN
                errors.add('Password must be 8-20 characters with uppercase, lowercase, and number')
            END IF
            
        CASE 'set_name':
            IF isEmpty(input) THEN
                errors.add('Set name is required')
            ELSE IF length(input) > 100 THEN
                errors.add('Set name must be less than 100 characters')
            END IF
            
        CASE 'score':
            IF !isNumber(input) OR input < 0 OR input > 100 THEN
                errors.add('Score must be a number between 0 and 100')
            END IF
    END SWITCH
    
    RETURN errors
END FUNCTION

FUNCTION isValidEmail(email)
    emailPattern = '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
    RETURN email.matches(emailPattern)
END FUNCTION

FUNCTION isValidPassword(password)
    // Check length
    IF length(password) < 8 OR length(password) > 20 THEN
        RETURN false
    END IF
    
    // Check for uppercase
    IF !password.matches('.*[A-Z].*') THEN
        RETURN false
    END IF
    
    // Check for lowercase
    IF !password.matches('.*[a-z].*') THEN
        RETURN false
    END IF
    
    // Check for number
    IF !password.matches('.*[0-9].*') THEN
        RETURN false
    END IF
    
    RETURN true
END FUNCTION
```

### 8.2 Business Rule Validation

```pseudocode
FUNCTION validateBusinessRules(action, data)
    errors = []
    
    SWITCH action
        CASE 'create_set':
            // Check daily limit
            dailySetCount = getDailySetCount(data.userId)
            IF dailySetCount >= MAX_DAILY_SETS THEN
                errors.add('Daily set creation limit exceeded')
            END IF
            
        CASE 'complete_cycle':
            // Check cycle status
            IF data.cycle.status != 'in_progress' THEN
                errors.add('Cycle must be in progress to complete')
            END IF
            
            // Check score validity
            IF data.score < 0 OR data.score > 100 THEN
                errors.add('Score must be between 0 and 100')
            END IF
            
        CASE 'reschedule_reminder':
            // Check daily reminder limit
            dailyReminderCount = getDailyReminderCount(data.userId, data.scheduledDate)
            IF dailyReminderCount >= MAX_DAILY_REMINDERS THEN
                errors.add('Daily reminder limit exceeded')
            END IF
            
            // Check future date
            IF data.scheduledDate <= getCurrentDate() THEN
                errors.add('Reminder must be scheduled for future date')
            END IF
    END SWITCH
    
    RETURN errors
END FUNCTION
```

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Technical Lead  
**Stakeholders**: Development Team, QA Team
